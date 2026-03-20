package com.example.revconnect.service;

import java.util.List;


import com.example.revconnect.dto.LikeResponse;


public interface LikeService {

    String likePost(Long userId, Long postId);

    String unlikePost(Long userId, Long postId);
    
    Long getLikeCount(Long postId);
    
    List<LikeResponse> getAllLikes();
    

}