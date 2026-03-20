package com.example.revconnect.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.revconnect.entity.Connection;
import com.example.revconnect.entity.NotificationType;
import com.example.revconnect.entity.User;
import com.example.revconnect.repository.ConnectionRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.service.ConnectionService;
import com.example.revconnect.service.NotificationService;

import jakarta.transaction.Transactional;

@Service
public class ConnectionServiceImpl implements ConnectionService{
	
    private ConnectionRepository connectionRepository;

    private UserRepository userRepository;

    private NotificationService notificationService;

    public ConnectionServiceImpl(ConnectionRepository theConnectionRepository, UserRepository theUserRepository,
			NotificationService theNotificationService) {
		this.connectionRepository = theConnectionRepository;
		this.userRepository = theUserRepository;
		this.notificationService = theNotificationService;
	}
    
    @Override
    @Transactional
	public Connection sendRequest(Long senderId, Long receiverId) {

        User sender = userRepository.findById(senderId)
        		.orElseThrow(()-> new RuntimeException("sender not found"));
        
        User receiver = userRepository.findById(receiverId)
        		.orElseThrow(()-> new RuntimeException("receiver not found"));

        Connection connection = new Connection();
        
        connection.setSender(sender);
        connection.setReceiver(receiver);
        connection.setStatus("PENDING");

        Connection saved = connectionRepository.save(connection);

        notificationService.createNotification(
                sender,
                receiver,
                NotificationType.CONNECTION_REQUEST,
                sender.getUserName() + " sent you a connection request"
        );

        return saved;
    }
    
    @Override
    @Transactional
    public Connection acceptRequest(Long connectionId) {

        Connection connection = connectionRepository.findById(connectionId)
        		.orElseThrow(()-> new RuntimeException("connection not found"));
        
        connection.setStatus("ACCEPTED");

        Connection saved = connectionRepository.save(connection);

        notificationService.createNotification(
                connection.getReceiver(),
                connection.getSender(),
                NotificationType.CONNECTION_ACCEPTED,
                connection.getReceiver().getUserName() + " accepted your connection request"
        );

        return saved;
    }
    
    @Override
    @Transactional
    public Connection rejectRequest(Long connectionId) {
    	
        Connection connection = connectionRepository.findById(connectionId)
        		.orElseThrow(()-> new RuntimeException("connection not found"));
        
        connection.setStatus("REJECTED");
        return connectionRepository.save(connection);
    }
    
    @Override
    @Transactional
    public List<Connection> getConnections(Long userId) {
    	
        User user = userRepository.findById(userId)
        		.orElseThrow(()-> new RuntimeException("user not found"));
        
        return connectionRepository.findByReceiverAndStatus(user, "ACCEPTED");
    }
    
    @Override
    @Transactional
    public List<Connection> getPendingRequests(Long userId) {
    	
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }
    
    @Override
    @Transactional
    public String removeConnection(Long userId, Long connectionUserId) {
    	
        Connection connection = connectionRepository
                .findBySenderUserIdAndReceiverUserIdOrSenderUserIdAndReceiverUserId(
                        userId, connectionUserId,
                        connectionUserId, userId
                );

        if (connection == null) {
            throw new RuntimeException("Connection not found");
        }

        connectionRepository.delete(connection);
        return "Connection removed successfully";
    }
    
    @Override
    @Transactional
    public List<Connection> getSentRequests(Long userId) {
    	
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Connection> connections = connectionRepository.findBySender(user);
        List<Connection> pendingConnections = new ArrayList<>();

        for (Connection connection : connections) {
            if ("PENDING".equals(connection.getStatus())) {
                pendingConnections.add(connection);
            }
        }

        return pendingConnections;
    }
}
