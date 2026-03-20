package com.example.revconnect.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.LikeResponse;
import com.example.revconnect.entity.Like;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.Post;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.LikeRepository;
import com.example.revconnect.repository.PostRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.LikeService;
import com.example.revconnect.service.NotificationService;

@Service
public class LikeServiceImpl implements LikeService{
	
	@Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private NotificationService notificationService;

    // Like post
    public String likePost(Long userId, Long postId) {

        if (likeRepository.findByUserUserIdAndPostPostId(userId, postId).isPresent()) {
            throw new BadRequestException("Post already liked");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);
        notificationService.createNotification(
                user,
                post.getUser(),
                NotificationType.LIKE,
                user.getUserName() + " liked your post"
        );

        Long likeCount = likeRepository.countByPostPostId(postId);

        return "Post liked successfully. Total Likes: " + likeCount;
    }

    // Unlike post
    public String unlikePost(Long userId, Long postId) {

        Like like = likeRepository
                .findByUserUserIdAndPostPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);

        Long likeCount = likeRepository.countByPostPostId(postId);

        return "Post unliked successfully. Total Likes: " + likeCount;
    }

    // Like count
    public Long getLikeCount(Long postId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return likeRepository.countByPostPostId(postId);
    }

    // Get all likes (admin/debug)
    public List<LikeResponse> getAllLikes() {

        return likeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    private LikeResponse mapToDto(Like like) {

        LikeResponse dto = new LikeResponse();

        dto.setLikeId(like.getLikeId());
        dto.setUserId(like.getUser().getUserId());
        dto.setUserName(like.getUser().getUserName());
        dto.setPostId(like.getPost().getPostId());

        return dto;
    }
}
