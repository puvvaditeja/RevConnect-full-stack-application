package com.example.revconnect.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.revconnect.dto.PostAnalyticsResponse;
import com.example.revconnect.dto.PostResponse;
import com.example.revconnect.entity.Post;



public interface PostService {

   
    public PostResponse createPost(Long userId, Post post);
    
    public List<Post> getAllPosts();
    
    public Post getPostById(Long postId);
    
    public List<Post> getPostsByUser(Long viewerId, Long targetUserId);
    

    public List<Post> getPostsByUser(Long userId);
    
    public String editPost(Long userId, Long postId, String newContent);
    
    public String deletePost(Long userId, Long postId);
    
    public List<Post> getTrendingPosts();
    
    public void unlikePost(Long userId, Long postId);
    
    public void updatePost(Long postId, Long userId, String content);
    
    public List<Post> searchPosts(String keyword);
    
    public void sharePost(Long postId, Long userId);
    
    public Page<PostResponse> getFeed(Long userId, Pageable pageable);
    
    public List<Map<String, Object>> getTrendingHashtags();
    
    public List<PostResponse> getFilteredFeed(Long userId, String type);
    
    public String pinPost(Long userId, Long postId);
    
    public String unpinPost(Long userId, Long postId);

    public Map<String, Object> getEngagementMetrics(Long postId);
    
    public Post viewPost(Long postId);
    
    public List<PostResponse> searchByHashtagResponse(String tag);

    public Page<PostResponse> getPosts(Pageable pageable);

    public Page<PostResponse> getSortedPosts(Pageable pageable);

    public PostAnalyticsResponse getPostAnalytics(Long postId);
}