package com.example.revconnect.service;

import java.util.List;

import com.example.revconnect.dto.PostResponse;

public interface SavedPostService {


    public String savePost(Long userId, Long postId);
    
    public String unsavePost(Long userId, Long postId);
    
    public List<PostResponse> getSavedPosts(Long userId);
    
}