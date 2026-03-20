package com.example.revconnect.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.revconnect.dto.FollowResponse;
import com.example.revconnect.entity.Follow;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.FollowService;
import com.example.revconnect.service.NotificationService;

@Service
public class FollowServiceImpl implements FollowService{
	
	 private final FollowRepository followRepository;
	    private final NotificationService notificationService;
	    private final UserRepository userRepository;

	    public FollowServiceImpl(
	            FollowRepository followRepository,
	            NotificationService notificationService,
	            UserRepository userRepository) {

	        this.followRepository = followRepository;
	        this.notificationService = notificationService;
	        this.userRepository = userRepository;
	    }

	    // Follow user
	    public String followUser(Long followerId, Long followingId) {

	        if (followerId.equals(followingId)) {
	            throw new BadRequestException("You cannot follow yourself");
	        }

	        User follower = userRepository.findById(followerId)
	                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));

	        User following = userRepository.findById(followingId)
	                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found"));

	        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
	            throw new BadRequestException("Already following this user");
	        }

	        Follow follow = new Follow();
	        follow.setFollower(follower);
	        follow.setFollowing(following);
	        follow.setCreatedAt(LocalDateTime.now());

	        followRepository.save(follow);

	        notificationService.createNotification(
	                follower,
	                following,
	                NotificationType.FOLLOW,
	                follower.getUserName() + " started following you"
	        );

	        return "Followed successfully";
	    }

	    // Unfollow user
	    public String unfollowUser(Long followerId, Long followingId) {

	        User follower = userRepository.findById(followerId)
	                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));

	        User following = userRepository.findById(followingId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        Follow follow = followRepository
	                .findByFollowerAndFollowing(follower, following)
	                .orElseThrow(() -> new BadRequestException("You are not following this user"));

	        followRepository.delete(follow);

	        return "Unfollowed successfully";
	    }

	    // Get followers
	    public List<FollowResponse> getFollowers(Long userId) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        return followRepository.findByFollowing(user)
	                .stream()
	                .map(this::mapToDto)
	                .toList();
	    }

	    // Get following
	    public List<FollowResponse> getFollowing(Long userId) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        return followRepository.findByFollower(user)
	                .stream()
	                .map(follow -> {
	                    FollowResponse dto = new FollowResponse();
	                    dto.setFollowingId(follow.getFollowing().getUserId());
	                    dto.setFollowingName(follow.getFollowing().getUserName());
	                    return dto;
	                })
	                .toList();
	    }

	    // Followers count
	    public long getFollowersCount(Long userId) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        return followRepository.countByFollowing(user);
	    }

	    // Following count
	    public long getFollowingCount(Long userId) {

	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	        return followRepository.countByFollower(user);
	    }

	    // Remove follower
	    public String removeFollower(Long targetUserId, Long followerId) {

	        Follow follow = followRepository
	                .findByFollowerUserIdAndFollowingUserId(followerId, targetUserId)
	                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));

	        followRepository.delete(follow);

	        return "Follower removed successfully";
	    }

	    // Follower demographics
	    public Map<String, Long> getFollowerDemographics(Long userId) {

	        List<Object[]> results = followRepository.getFollowerDemographics(userId);

	        Map<String, Long> response = new HashMap<>();

	        for (Object[] row : results) {
	            response.put(row[0].toString(), (Long) row[1]);
	        }

	        return response;
	    }

	    // DTO mapper
	    private FollowResponse mapToDto(Follow follow) {

	        FollowResponse dto = new FollowResponse();

	        dto.setFollowerId(follow.getFollower().getUserId());
	        dto.setFollowerName(follow.getFollower().getUserName());
	        dto.setFollowingId(follow.getFollowing().getUserId());
	        dto.setFollowingName(follow.getFollowing().getUserName());

	        return dto;
	    }
}
