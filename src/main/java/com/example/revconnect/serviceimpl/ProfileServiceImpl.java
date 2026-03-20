package com.example.revconnect.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.ProfileDTO;
import com.example.revconnect.entity.Profile;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.PostRepository;
import com.example.revconnect.repository.ProfileRepository;
import com.example.revconnect.repository.ShareRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService{
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProfileDTO dto = new ProfileDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setAccountType(user.getAccountType().name());
        dto.setTotalPosts(postRepository.countByUser(user));
        dto.setTotalFollowers(followRepository.countByFollowing(user));
        dto.setTotalFollowing(followRepository.countByFollower(user));
        dto.setTotalShares(shareRepository.countByUser(user));

        profileRepository.findByUserUserId(userId).ifPresent(profile -> {
            dto.setName(profile.getName());
            dto.setBio(profile.getBio());
            dto.setProfilePic(profile.getProfilePic());
            dto.setWebsiteLink(profile.getWebsiteLink());
            dto.setLocation(profile.getLocation());
        });

        return dto;
    }

    public ProfileDTO updateProfile(Long userId, Profile updatedProfile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Profile profile = profileRepository.findByUserUserId(userId)
                .orElse(new Profile());

        profile.setUser(user);

        if (updatedProfile.getName() != null)
            profile.setName(updatedProfile.getName());
        if (updatedProfile.getBio() != null)
            profile.setBio(updatedProfile.getBio());
        if (updatedProfile.getProfilePic() != null)
            profile.setProfilePic(updatedProfile.getProfilePic());
        if (updatedProfile.getWebsiteLink() != null)
            profile.setWebsiteLink(updatedProfile.getWebsiteLink());
        if (updatedProfile.getLocation() != null)
            profile.setLocation(updatedProfile.getLocation());

        profileRepository.save(profile);
        return getProfile(userId);
    }
}
