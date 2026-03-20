package com.example.revconnect.service;

import java.util.List;
import java.util.Map;

import com.example.revconnect.dto.UserSearchResponse;
import com.example.revconnect.entity.User;

public interface UserService {

    
    public User registerUser(User user);

    public User loginUser(String email, String password);

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public User updateUser(Long id, User newUser);

    public void deleteUser(Long id) ;
    
    public List<UserSearchResponse> searchUsers(String keyword);
    
    public String updatePrivacy(Long userId, boolean isPrivate);
    
    public Map<String, Long> getFollowerDemographics(Long userId);
    
    public User updateBusinessProfile(Long userId, User updatedUser);
    
    public String getSecurityQuestion(String email);
    
    public void resetPassword(com.example.revconnect.dto.ResetPasswordRequest request);
}