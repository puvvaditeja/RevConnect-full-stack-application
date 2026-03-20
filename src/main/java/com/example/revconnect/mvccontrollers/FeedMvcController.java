package com.example.revconnect.mvccontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.dto.ProfileDTO;
import com.example.revconnect.entity.User;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.FollowService;
import com.example.revconnect.service.NotificationService;
import com.example.revconnect.service.PostService;
import com.example.revconnect.service.ProfileService;

@Controller
@RequestMapping("/feed")
public class FeedMvcController {

    @Autowired
    private PostService postService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FollowService followService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Main feed page — shows paginated posts for the logged-in user.
     * userId is passed as a request param (in a real app this would come from session/JWT).
     */
    @GetMapping
    public String showFeed(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> feedPage = postService.getFeed(userId, pageable);

        ProfileDTO profile = profileService.getProfile(userId);

        long followerCount = followService.getFollowersCount(userId);
        long followingCount = followService.getFollowingCount(userId);
        long unreadNotifications = notificationService.getUnreadCount(userId);

        // Suggested friends: all users excluding current user, limited to 5
        List<User> allUsers = userRepository.findAll();
        List<ProfileDTO> suggestedFriends = allUsers.stream()
                .filter(u -> !u.getUserId().equals(userId))
                .limit(5)
                .map(u -> {
                    try { return profileService.getProfile(u.getUserId()); }
                    catch (Exception e) {
                        ProfileDTO dto = new ProfileDTO();
                        dto.setUserId(u.getUserId());
                        dto.setUserName(u.getUserName());
                        return dto;
                    }
                })
                .toList();

        model.addAttribute("posts", feedPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", feedPage.getTotalPages());
        model.addAttribute("profile", profile);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("unreadNotifications", unreadNotifications);
        model.addAttribute("suggestedFriends", suggestedFriends);
        model.addAttribute("userId", userId);

        return "feed";
    }

    /**
     * Trending posts page.
     */
    @GetMapping("/trending")
    public String showTrending(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<PostResponse> trendingPosts = postService.getAllPosts()
                .stream()
                .map(PostResponse::new)
                .toList();

        model.addAttribute("posts", trendingPosts);
        model.addAttribute("userId", userId);
        model.addAttribute("pageTitle", "Trending Posts");

        ProfileDTO profile = profileService.getProfile(userId);
        model.addAttribute("profile", profile);

        return "feed";
    }

    /**
     * Search posts by keyword or hashtag.
     */
    @GetMapping("/search")
    public String searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<PostResponse> results;
        if (keyword.startsWith("#")) {
            results = postService.searchByHashtagResponse(keyword);
        } else {
            results = postService.searchPosts(keyword)
                    .stream()
                    .map(PostResponse::new)
                    .toList();
        }

        ProfileDTO profile = profileService.getProfile(userId);
        model.addAttribute("posts", results);
        model.addAttribute("userId", userId);
        model.addAttribute("profile", profile);
        model.addAttribute("searchKeyword", keyword);

        return "feed";
    }
}