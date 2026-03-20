package com.example.revconnect.service;

import com.example.revconnect.entity.NotificationPreference;

public interface NotificationPreferenceService {

    NotificationPreference getPreferences(Long userId);
    
    NotificationPreference updatePreferences(Long userId,NotificationPreference newPref) ;
}