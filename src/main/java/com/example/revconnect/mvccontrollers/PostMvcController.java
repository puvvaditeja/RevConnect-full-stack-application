package com.example.revconnect.mvccontrollers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revconnect.dto.CommentResponse;
import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.entity.Post;
import com.example.revconnect.repository.LikeRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.CommentService;
import com.example.revconnect.service.LikeService;
import com.example.revconnect.service.PostService;
import com.example.revconnect.service.SavedPostService;
import com.example.revconnect.service.ShareService;

@Controller
@RequestMapping("/posts")
public class PostMvcController {

    private PostService postService;

    private CommentService commentService;

    private LikeService likeService;

    private SavedPostService savedPostService;

    private ShareService shareService;

    private LikeRepository likeRepository;   
    
    private UserRepository userRepository;
    
    public PostMvcController() {
   	}
    
    @Autowired
    public PostMvcController(PostService thePostService, CommentService theCommentService, LikeService theLikeService,
			SavedPostService theSavedPostService, ShareService theShareService, 
			LikeRepository theLikeRepository, UserRepository theUserRepository) {
		this.postService = thePostService;
		this.commentService = theCommentService;
		this.likeService = theLikeService;
		this.savedPostService = theSavedPostService;
		this.shareService = theShareService;
		this.likeRepository = theLikeRepository;
		this.userRepository = theUserRepository;
	}


	@GetMapping("/{postId}")
    public String viewPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        Post post = postService.viewPost(postId);
        List<CommentResponse> comments = commentService.getCommentsByPost(postId);
        Long likeCount = likeService.getLikeCount(postId);
        boolean likedByUser = likeRepository
                .findByUserUserIdAndPostPostId(userId, postId)
                .isPresent();

        model.addAttribute("post", new PostResponse(post));
        model.addAttribute("comments", comments);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("userId", userId);

        return "post-detail";
    }


	@GetMapping("/create")
	public String showCreatePost(
	        @RequestParam(defaultValue = "1") Long userId,
	        Model model) {

	    model.addAttribute("post", new Post());
	    model.addAttribute("userId", userId);

	    // pass accountType to template ✅
	    userRepository.findById(userId).ifPresent(user ->
	        model.addAttribute("accountType", user.getAccountType().name())
	    );

	    return "create-post";
	}


    @PostMapping("/create")
    public String createPost(
            @RequestParam Long userId,
            @ModelAttribute Post post,
            RedirectAttributes redirectAttributes) {

        postService.createPost(userId, post);
        redirectAttributes.addFlashAttribute("successMessage", "Post created successfully!");
        return "redirect:/feed?userId=" + userId;
    }

    @GetMapping("/{postId}/edit")
    public String showEditPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        Post post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("userId", userId);
        return "edit-post";
    }


    @PostMapping("/{postId}/edit")
    public String editPost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        postService.editPost(userId, postId, content);
        redirectAttributes.addFlashAttribute("successMessage", "Post updated successfully!");
        return "redirect:/feed?userId=" + userId;
    }


    @PostMapping("/{postId}/delete")
    public String deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        postService.deletePost(userId, postId);
        redirectAttributes.addFlashAttribute("successMessage", "Post deleted.");
        return "redirect:/feed?userId=" + userId;
    }


    @PostMapping("/{postId}/like")
    public String likePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "") String redirectTo,
            RedirectAttributes redirectAttributes) {

        try {
            likeService.likePost(userId, postId);
        } catch (Exception e) {
            // Already liked — silently ignore
        }

        String redirect = redirectTo.isEmpty() ? "/feed?userId=" + userId : redirectTo;
        return "redirect:" + redirect;
    }

    @PostMapping("/{postId}/unlike")
    public String unlikePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "") String redirectTo,
            RedirectAttributes redirectAttributes) {

        try {
            likeService.unlikePost(userId, postId);
        } catch (Exception e) {
            // Not liked — silently ignore
        }

        String redirect = redirectTo.isEmpty() ? "/feed?userId=" + userId : redirectTo;
        return "redirect:" + redirect;
    }

    @PostMapping("/{postId}/comment")
    public String addComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        commentService.addComment(userId, postId, content);
        return "redirect:/posts/" + postId + "?userId=" + userId;
    }
    
    @PostMapping("/comments/{commentId}/reply")
    public String replyToComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {
 
        try {
            commentService.replyToComment(userId, commentId, content);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/posts/" + postId + "?userId=" + userId;
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId,
            @RequestParam Long postId,
            RedirectAttributes redirectAttributes) {

    	try {
            commentService.deleteComment(commentId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Comment deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this comment.");
        }
        return "redirect:/posts/" + postId + "?userId=" + userId;
    }

    @PostMapping("/{postId}/save")
    public String savePost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        try {
            savedPostService.savePost(userId, postId);
            redirectAttributes.addFlashAttribute("successMessage", "Post saved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/feed?userId=" + userId;
    }

    @GetMapping("/{postId}/share-sheet")
    public String showShareSheet(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "") String redirectTo,
            Model model) {

        Post post = postService.getPostById(postId);
        java.util.List<com.example.revconnect.dto.ShareTargetDTO> targets =
                shareService.getShareTargets(userId, postId);

        model.addAttribute("post", new PostResponse(post));
        model.addAttribute("targets", targets);
        model.addAttribute("userId", userId);
        model.addAttribute("redirectTo", redirectTo);

        return "share-sheet";
    }

    @PostMapping("/{postId}/share")
    public String shareToFeed(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "") String redirectTo,
            RedirectAttributes redirectAttributes) {

        try {
            shareService.sharePost(userId, postId);
            redirectAttributes.addFlashAttribute("successMessage", "Post shared to your feed!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String back = redirectTo.isEmpty() ? "/feed?userId=" + userId : redirectTo;
        return "redirect:" + back;
    }

    @PostMapping("/{postId}/share-to/{recipientId}")
    public String shareToUser(
            @PathVariable Long postId,
            @PathVariable Long recipientId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "") String caption,
            @RequestParam(defaultValue = "") String redirectTo,
            RedirectAttributes redirectAttributes) {

        try {
            String result = shareService.sharePostToUser(userId, postId, recipientId, caption);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String back = redirectTo.isEmpty() ? "/feed?userId=" + userId : redirectTo;
        return "redirect:" + back;
    }

    @PostMapping("/{postId}/pin")
    public String pinPost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        postService.pinPost(userId, postId);
        return "redirect:/profile?userId=" + userId;
    }

    @PostMapping("/{postId}/unpin")
    public String unpinPost(
            @PathVariable Long postId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        postService.unpinPost(userId, postId);
        return "redirect:/profile?userId=" + userId;
    }

    @GetMapping("/saved")
    public String savedPosts(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<PostResponse> savedPosts = savedPostService.getSavedPosts(userId);
        model.addAttribute("posts", savedPosts);
        model.addAttribute("userId", userId);
        model.addAttribute("pageTitle", "Saved Posts");
        return "saved-posts";
    }
}
