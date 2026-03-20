package com.example.revconnect.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.entity.NotificationPreference;
import com.example.revconnect.entity.User;
import com.example.revconnect.repository.NotificationPreferenceRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.NotificationPreferenceService;

@Service
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService{
	
	@Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private UserRepository userRepository;

    public NotificationPreference getPreferences(Long userId) {

        return preferenceRepository
                .findByUserUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    NotificationPreference pref = new NotificationPreference();
                    pref.setUser(user);
                    return preferenceRepository.save(pref);
                });
    }

    public NotificationPreference updatePreferences(
            Long userId,
            NotificationPreference newPref) {

        NotificationPreference pref = getPreferences(userId);

        pref.setLikeNotifications(newPref.isLikeNotifications());
        pref.setCommentNotifications(newPref.isCommentNotifications());
        pref.setFollowNotifications(newPref.isFollowNotifications());
        pref.setShareNotifications(newPref.isShareNotifications());

        return preferenceRepository.save(pref);
    }
}
