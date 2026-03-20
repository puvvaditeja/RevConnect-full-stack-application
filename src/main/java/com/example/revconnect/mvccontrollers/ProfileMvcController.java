package com.example.revconnect.mvccontrollers;

import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.dto.ProfileDTO;
import com.example.revconnect.entity.Connection;
import com.example.revconnect.entity.Profile;
import com.example.revconnect.repository.ConnectionRepository;
import com.example.revconnect.service.ConnectionService;
import com.example.revconnect.service.FollowService;
import com.example.revconnect.service.PostService;
import com.example.revconnect.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileMvcController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowService followService;

    @Autowired
    private ConnectionRepository connectionRepository;

    /**
     * View a user's profile with their posts.
     * If targetUserId is not supplied, shows the logged-in user's own profile.
     */
    @GetMapping
    public String showProfile(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(required = false) Long targetUserId,
            Model model) {

        Long profileUserId = (targetUserId != null) ? targetUserId : userId;

        ProfileDTO profile = profileService.getProfile(profileUserId);

        List<PostResponse> userPosts;
        try {
            userPosts = postService.getPostsByUser(userId, profileUserId)
                    .stream()
                    .map(PostResponse::new)
                    .toList();
        } catch (Exception e) {
            userPosts = List.of();
            model.addAttribute("privateAccountMessage", "This account is private.");
        }

        boolean isFollowing = followService.getFollowing(userId)
                .stream()
                .anyMatch(f -> f.getFollowingId().equals(profileUserId));

        boolean isOwnProfile = userId.equals(profileUserId);

        // Connection status
        String connectionStatus = "NONE";
        Long connectionId = null;
        if (!isOwnProfile) {
            Connection sentByMe = connectionRepository.findBySenderUserIdAndReceiverUserId(userId, profileUserId);
            Connection sentByThem = connectionRepository.findBySenderUserIdAndReceiverUserId(profileUserId, userId);
            if (sentByMe != null) {
                connectionStatus = sentByMe.getStatus(); // PENDING or ACCEPTED
                connectionId = sentByMe.getConnectionId();
            } else if (sentByThem != null) {
                connectionStatus = sentByThem.getStatus().equals("ACCEPTED") ? "ACCEPTED" : "RECEIVED";
                connectionId = sentByThem.getConnectionId();
            }
        }

        model.addAttribute("profile", profile);
        model.addAttribute("posts", userPosts);
        model.addAttribute("userId", userId);
        model.addAttribute("profileUserId", profileUserId);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("isOwnProfile", isOwnProfile);
        model.addAttribute("connectionStatus", connectionStatus);
        model.addAttribute("connectionId", connectionId);

        return "profile";
    }

    /**
     * Show edit profile form.
     */
    @GetMapping("/edit")
    public String showEditProfile(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        ProfileDTO profile = profileService.getProfile(userId);
        model.addAttribute("profile", profile);
        model.addAttribute("userId", userId);
        return "edit-profile";
    }

    /**
     * Submit profile updates.
     */
    @PostMapping("/edit")
    public String updateProfile(
            @RequestParam Long userId,
            @ModelAttribute Profile updatedProfile,
            RedirectAttributes redirectAttributes) {

        profileService.updateProfile(userId, updatedProfile);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile?userId=" + userId;
    }
}