package com.example.revconnect.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.revconnect.entity.Connection;
import com.example.revconnect.service.ConnectionService;

@RestController
@RequestMapping("/connections")
public class ConnectionController {

    private ConnectionService connectionService;

    public ConnectionController(ConnectionService theConnectionService) {
		this.connectionService = theConnectionService;
	}

	@PostMapping("/send")
    public Connection sendRequest(@RequestParam Long senderId,
                                  @RequestParam Long receiverId) {

        return connectionService.sendRequest(senderId, receiverId);
    }

    @PutMapping("/accept/{id}")
    public Connection acceptRequest(@PathVariable Long id) {

        return connectionService.acceptRequest(id);
    }

    @PutMapping("/reject/{id}")
    public Connection rejectRequest(@PathVariable Long id) {

        return connectionService.rejectRequest(id);
    }

    @GetMapping("/{userId}")
    public List<Connection> getConnections(@PathVariable Long userId) {

        return connectionService.getConnections(userId);
    }
    
    @GetMapping("/pending/{userId}")
    public List<Connection> getPendingRequests(@PathVariable Long userId) {

        return connectionService.getPendingRequests(userId);
    }
    
    @DeleteMapping("/remove/{userId}/{connectionUserId}")
    public String removeConnection(@PathVariable Long userId,
                                   @PathVariable Long connectionUserId) {

        return connectionService.removeConnection(userId, connectionUserId);
    }
    
    @GetMapping("/sent/{userId}")
    public List<Connection> getSentRequests(@PathVariable Long userId) {
    	
        return connectionService.getSentRequests(userId);
    }
}