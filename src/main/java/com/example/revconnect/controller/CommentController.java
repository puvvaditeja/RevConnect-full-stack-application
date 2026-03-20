package com.example.revconnect.controller;

import com.example.revconnect.dto.CommentResponse;
import com.example.revconnect.entity.Comment;
import com.example.revconnect.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentService commentService;
    
    public CommentController(CommentService theCommentService) {
    	
    	this.commentService = theCommentService;
    }

    // Add Comment
    @PostMapping("/user/{userId}/post/{postId}")
    public CommentResponse addComment(@PathVariable Long userId,
                                      @PathVariable Long postId,
                                      @RequestBody String content) {

        return commentService.addComment(userId, postId, content);
    }

    // Get Comments of Post
    @GetMapping("/post/{postId}")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
    	
        return commentService.getCommentsByPost(postId);
    }
    
    @DeleteMapping("/user/{userId}/comment/{commentId}")
    public String deleteComment(@PathVariable Long userId,
                                @PathVariable Long commentId) {

        commentService.deleteComment(commentId, userId);
     
        return "Comment deleted successfully";
    }
    
    @PostMapping("/reply/{commentId}")
    public Comment replyToComment(@PathVariable Long commentId,
                                  @RequestParam Long userId,
                                  @RequestBody String content) {

        return commentService.replyToComment(userId, commentId, content);
    }
}