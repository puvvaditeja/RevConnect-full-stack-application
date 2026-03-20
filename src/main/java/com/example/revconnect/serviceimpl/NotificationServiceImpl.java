package com.example.revconnect.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.NotificationResponse;
import com.example.revconnect.entity.Notification;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.User;
import com.example.revconnect.repository.NotificationRepository;
import com.example.revconnect.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService{
	
	@Autowired
    private NotificationRepository notificationRepository;
    
    

    public void createNotification(User sender,
                                   User receiver,
                                   NotificationType type,
                                   String message) {

        // Prevent self notification
        if (sender.getUserId().equals(receiver.getUserId())) {
            return;
        }

        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {

        return notificationRepository
                .findByReceiverUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public String markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);

        return "Notification marked as read";
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository
                .countByReceiverUserIdAndIsReadFalse(userId);
    }
    private NotificationResponse mapToDto(Notification notification) {

        NotificationResponse dto = new NotificationResponse();

        dto.setNotificationId(notification.getNotificationId());

        dto.setSenderId(notification.getSender().getUserId());
        dto.setSenderName(notification.getSender().getUserName());

        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().toString());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }

    public List<NotificationResponse> getUnreadNotifications(Long userId) {

        List<Notification> notifications =
                notificationRepository.findByReceiverUserIdAndIsReadFalse(userId);

        return notifications.stream()
                .map(this::mapToDto)
                .toList();
    }
}
