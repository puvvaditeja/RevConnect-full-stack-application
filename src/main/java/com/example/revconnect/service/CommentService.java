package com.example.revconnect.service;

import java.util.List;

import com.example.revconnect.dto.CommentResponse;
import com.example.revconnect.entity.Comment;

public interface CommentService {

    CommentResponse addComment(Long userId, Long postId, String content);

    List<CommentResponse> getCommentsByPost(Long postId);

    void deleteComment(Long commentId, Long userId);

    Comment replyToComment(Long userId, Long commentId, String content);
    
}