package com.example.revconnect.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    private Long commentId;
    private Long userId;
    private String userName;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
    
	public List<CommentResponse> getReplies() {
		return replies;
	}
	public void setReplies(List<CommentResponse> replies) {
		this.replies = replies;
	}
	public String getUserName() { 
		return userName; 
	}
	public void setUserName(String userName) { 
		this.userName = userName; 
	}
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}