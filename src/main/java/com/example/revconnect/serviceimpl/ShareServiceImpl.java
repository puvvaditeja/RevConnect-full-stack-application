package com.example.revconnect.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.ShareTargetDTO;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.Post;
import com.example.revconnect.entity.Profile;
import com.example.revconnect.entity.Share;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.PostRepository;
import com.example.revconnect.repository.ProfileRepository;
import com.example.revconnect.repository.ShareRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.NotificationService;
import com.example.revconnect.service.ShareService;

@Service
public class ShareServiceImpl implements ShareService{
	
	@Autowired private ShareRepository     shareRepository;
    @Autowired private UserRepository      userRepository;
    @Autowired private PostRepository      postRepository;
    @Autowired private FollowRepository    followRepository;
    @Autowired private ProfileRepository   profileRepository;
    @Autowired private NotificationService notificationService;

    // ──────────────────────────────────────────────────────────────────
    // Original public share — records a share on the sharer's own feed
    // ──────────────────────────────────────────────────────────────────
    public String sharePost(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (shareRepository.findByUserAndPost(user, post).isPresent()) {
            throw new BadRequestException("You already shared this post");
        }

        Share share = new Share();
        share.setUser(user);
        share.setPost(post);
        share.setCreatedAt(LocalDateTime.now());
        shareRepository.save(share);

        if (!post.getUser().getUserId().equals(userId)) {
            notificationService.createNotification(
                    user, post.getUser(),
                    NotificationType.SHARE,
                    user.getUserName() + " shared your post"
            );
        }

        return "Post shared successfully";
    }

    // ──────────────────────────────────────────────────────────────────
    // Share to a SPECIFIC USER — sends them a notification with a
    // direct link to the post so it appears in their notifications feed
    // ──────────────────────────────────────────────────────────────────
    public String sharePostToUser(Long sharerId, Long postId, Long recipientId, String caption) {

        if (sharerId.equals(recipientId)) {
            throw new BadRequestException("You cannot share a post to yourself");
        }

        User sharer = userRepository.findById(sharerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sharer not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Also record as a public share (deduped)
        if (shareRepository.findByUserAndPost(sharer, post).isEmpty()) {
            Share share = new Share();
            share.setUser(sharer);
            share.setPost(post);
            share.setCreatedAt(LocalDateTime.now());
            shareRepository.save(share);

            // Notify post owner
            if (!post.getUser().getUserId().equals(sharerId)) {
                notificationService.createNotification(
                        sharer, post.getUser(),
                        NotificationType.SHARE,
                        sharer.getUserName() + " shared your post"
                );
            }
        }

        // Notify recipient with caption
        String msg = caption != null && !caption.isBlank()
                ? sharer.getUserName() + " shared a post with you: \"" + caption + "\""
                : sharer.getUserName() + " shared a post with you";

        notificationService.createNotification(
                sharer, recipient,
                NotificationType.SHARE,
                msg
        );

        return "Post shared with " + recipient.getUserName();
    }

    // ──────────────────────────────────────────────────────────────────
    // Build the list of users the sharer can send this post to.
    // We show: (1) users they follow, (2) their followers — deduplicated
    // and annotated with alreadyShared flag.
    // ──────────────────────────────────────────────────────────────────
    public List<ShareTargetDTO> getShareTargets(Long sharerId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User sharer = userRepository.findById(sharerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Collect unique candidates: people sharer follows + sharer's followers
        Map<Long, User> candidateMap = new LinkedHashMap<>();

        followRepository.findByFollower(sharer).forEach(f ->
                candidateMap.put(f.getFollowing().getUserId(), f.getFollowing()));

        followRepository.findByFollowing(sharer).forEach(f ->
                candidateMap.put(f.getFollower().getUserId(), f.getFollower()));

        // Remove the sharer themselves and the post author if they are the sharer
        candidateMap.remove(sharerId);

        // Check if sharer already shared this post (to mark the button)
        boolean sharerAlreadyShared = shareRepository.findByUserAndPost(sharer, post).isPresent();

        List<ShareTargetDTO> targets = new ArrayList<>();

        for (User candidate : candidateMap.values()) {

            String displayName = candidate.getUserName(); // fallback
            String profilePic  = null;

            // Try to get profile data
            var profileOpt = profileRepository.findByUserUserId(candidate.getUserId());
            if (profileOpt.isPresent()) {
                Profile p = profileOpt.get();
                if (p.getName() != null && !p.getName().isBlank()) {
                    displayName = p.getName();
                }
                profilePic = p.getProfilePic();
            }

            targets.add(new ShareTargetDTO(
                    candidate.getUserId(),
                    candidate.getUserName(),
                    displayName,
                    profilePic,
                    candidate.getAccountType().name(),
                    sharerAlreadyShared   // shared to feed already — individual DM is always available
            ));
        }

        return targets;
    }
}
