package com.example.revconnect.mvccontrollers;

import com.example.revconnect.dto.NotificationResponse;
import com.example.revconnect.entity.Connection;
import com.example.revconnect.service.ConnectionService;
import com.example.revconnect.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationMvcController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ConnectionService connectionService;

    /**
     * View all notifications for the current user.
     */
    @GetMapping
    public String viewNotifications(
            @RequestParam(defaultValue = "1") Long userId,
            Model model) {

        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
        long unreadCount = notificationService.getUnreadCount(userId);
        List<Connection> pendingRequests = connectionService.getPendingRequests(userId);

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("userId", userId);

        return "notifications";
    }

    /**
     * Mark a notification as read, then redirect back.
     */
    @PostMapping("/{notificationId}/read")
    public String markAsRead(
            @PathVariable Long notificationId,
            @RequestParam(defaultValue = "1") Long userId,
            RedirectAttributes redirectAttributes) {

        notificationService.markAsRead(notificationId);
        return "redirect:/notifications?userId=" + userId;
    }

    /**
     * Mark all notifications as read.
     */
    @PostMapping("/read-all")
    public String markAllRead(
            @RequestParam(defaultValue = "1") Long userId,
            RedirectAttributes redirectAttributes) {

        List<NotificationResponse> unread = notificationService.getUnreadNotifications(userId);
        unread.forEach(n -> notificationService.markAsRead(n.getNotificationId()));
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications?userId=" + userId;
    }
}