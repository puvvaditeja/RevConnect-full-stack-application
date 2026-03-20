package com.example.revconnect.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revconnect.dto.FollowResponse;
import com.example.revconnect.entity.Follow;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.UserRepository;

public interface FollowService {

    
    public String followUser(Long followerId, Long followingId);
    
    public String unfollowUser(Long followerId, Long followingId);
    
    public List<FollowResponse> getFollowers(Long userId);
    
    public List<FollowResponse> getFollowing(Long userId);
    
    public long getFollowersCount(Long userId);
    
    public long getFollowingCount(Long userId);
    
    public String removeFollower(Long targetUserId, Long followerId);
    
    public Map<String, Long> getFollowerDemographics(Long userId);
    
}