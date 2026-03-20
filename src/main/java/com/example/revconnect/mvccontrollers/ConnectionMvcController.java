package com.example.revconnect.mvccontrollers;

import com.example.revconnect.entity.Connection;
import com.example.revconnect.service.ConnectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/mvc/connections")
public class ConnectionMvcController {

    private ConnectionService connectionService;

    public ConnectionMvcController(ConnectionService theConnectionService) {
		this.connectionService = theConnectionService;
	}

    @GetMapping
    public String viewConnections(
            @RequestParam Long userId,
            Model model) {

        List<Connection> connections = connectionService.getConnections(userId);
        List<Connection> pendingRequests = connectionService.getPendingRequests(userId);
        List<Connection> sentRequests = connectionService.getSentRequests(userId);

        model.addAttribute("connections", connections);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("sentRequests", sentRequests);
        model.addAttribute("userId", userId);

        return "connections";
    }

    @PostMapping("/send")
    public String sendRequest(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            RedirectAttributes redirectAttributes) {

        try {
            connectionService.sendRequest(senderId, receiverId);
            redirectAttributes.addFlashAttribute("successMessage", "Connection request sent!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile?userId=" + senderId + "&targetUserId=" + receiverId;
    }

    @PostMapping("/accept/{connectionId}")
    public String acceptRequest(
            @PathVariable Long connectionId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        try {
            connectionService.acceptRequest(connectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Connection accepted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/notifications?userId=" + userId;
    }

    @PostMapping("/reject/{connectionId}")
    public String rejectRequest(
            @PathVariable Long connectionId,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        try {
            connectionService.rejectRequest(connectionId);
            redirectAttributes.addFlashAttribute("successMessage", "Connection request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/notifications?userId=" + userId;
    }

    @PostMapping("/remove")
    public String removeConnection(
            @RequestParam Long userId,
            @RequestParam Long targetUserId,
            RedirectAttributes redirectAttributes) {

        try {
            connectionService.removeConnection(userId, targetUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Connection removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile?userId=" + userId + "&targetUserId=" + targetUserId;
    }
}