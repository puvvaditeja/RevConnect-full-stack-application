package com.example.revconnect.service;

import java.util.List;

import com.example.revconnect.entity.Connection;

public interface ConnectionService {

    Connection sendRequest(Long senderId, Long receiverId);

    Connection acceptRequest(Long connectionId);
    
    Connection rejectRequest(Long connectionId);

    List<Connection> getConnections(Long userId);

    List<Connection> getPendingRequests(Long userId);
    
    String removeConnection(Long userId, Long connectionUserId);

    List<Connection> getSentRequests(Long userId);
}