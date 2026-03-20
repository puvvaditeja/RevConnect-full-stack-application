package com.example.revconnect.service;

import java.util.List;

import com.example.revconnect.dto.NotificationResponse;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.User;

public interface NotificationService {

    public void createNotification(User sender,
                                   User receiver,
                                   NotificationType type,
                                   String message);
    
    public List<NotificationResponse> getUserNotifications(Long userId);
    
    public String markAsRead(Long notificationId);

    public long getUnreadCount(Long userId);
        
    public List<NotificationResponse> getUnreadNotifications(Long userId);
    
}