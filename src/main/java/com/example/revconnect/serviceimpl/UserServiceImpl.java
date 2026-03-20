package com.example.revconnect.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.ResetPasswordRequest;
import com.example.revconnect.dto.UserSearchResponse;
import com.example.revconnect.entity.AccountType;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.exception.ResourceNotFoundException;
import com.example.revconnect.repository.FollowRepository;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.security.JwtUtil;
import com.example.revconnect.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // REGISTER USER

    public User registerUser(User user) {

    	if (user.getUserName() == null ||
    		    user.getEmail() == null ||
    		    user.getPassword() == null ||
    		    user.getAccountType() == null ||
    		    user.getSecurityQuestion() == null ||
    		    user.getSecurityAnswer() == null) {

    		    throw new BadRequestException("Username, email, password, accountType, securityQuestion and securityAnswer are required");
    		}
        // Check duplicate email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        // PERSONAL account cleanup
        if (user.getAccountType() == AccountType.PERSONAL) {
            user.setCategory(null);
            user.setContactInfo(null);
            user.setBusinessAddress(null);
            user.setBusinessHours(null);
            user.setSocialLink(null);
            user.setProductsOrServices(null);
        }

        // CREATOR account cleanup
        if (user.getAccountType() == AccountType.CREATOR) {
            user.setBusinessAddress(null);
            user.setBusinessHours(null);
            user.setProductsOrServices(null);
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ================= LOGIN USER =================

    public User loginUser(String email, String password) {

        if (email == null || password == null) {
            throw new BadRequestException("Email and password required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return user;
    }

    // ================= GET ALL USERS =================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ================= GET USER BY ID =================

    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ================= UPDATE USER =================

    public User updateUser(Long id, User newUser) {

        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (newUser.getUserName() != null)
            oldUser.setUserName(newUser.getUserName());

        if (newUser.getEmail() != null)
            oldUser.setEmail(newUser.getEmail());

        if (newUser.getAccountType() != null)
            oldUser.setAccountType(newUser.getAccountType());

        if (newUser.getContactInfo() != null)
            oldUser.setContactInfo(newUser.getContactInfo());

        oldUser.setIsPrivate(newUser.getIsPrivate());

        if (newUser.getContactInfo() != null)
            oldUser.setContactInfo(newUser.getContactInfo());

        // Clean fields based on account type
        if (oldUser.getAccountType() == AccountType.PERSONAL) {

            oldUser.setCategory(null);
            oldUser.setContactInfo(null);
            oldUser.setBusinessAddress(null);
            oldUser.setBusinessHours(null);
            oldUser.setSocialLink(null);
            oldUser.setProductsOrServices(null);
        }

        if (oldUser.getAccountType() == AccountType.CREATOR) {

            oldUser.setBusinessAddress(null);
            oldUser.setBusinessHours(null);
            oldUser.setProductsOrServices(null);
        }

        return userRepository.save(oldUser);
    }

    // ================= DELETE USER =================

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.deleteById(id);
    }

    // ================= SEARCH USERS =================

    public List<UserSearchResponse> searchUsers(String keyword) {

        List<User> users = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found for keyword: " + keyword);
        }

        return users.stream()
                .map(UserSearchResponse::new)
                .toList();
    }

    // ================= UPDATE PRIVACY =================

    public String updatePrivacy(Long userId, boolean isPrivate) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsPrivate(isPrivate);

        userRepository.save(user);

        return "Account privacy updated successfully";
    }

    // ================= FOLLOWER DEMOGRAPHICS =================

    public Map<String, Long> getFollowerDemographics(Long userId) {

        List<Object[]> results = followRepository.getFollowerDemographics(userId);

        Map<String, Long> response = new HashMap<>();

        for (Object[] row : results) {

            String accountType = row[0].toString();
            Long count = (Long) row[1];

            response.put(accountType, count);
        }

        return response;
    }

    // ================= UPDATE BUSINESS PROFILE =================

    public User updateBusinessProfile(Long userId, User updatedUser) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updatedUser.getCategory() != null)
            user.setCategory(updatedUser.getCategory());

        if (updatedUser.getContactInfo() != null)
            user.setContactInfo(updatedUser.getContactInfo());

        if (updatedUser.getBusinessAddress() != null)
            user.setBusinessAddress(updatedUser.getBusinessAddress());

        if (updatedUser.getBusinessHours() != null)
            user.setBusinessHours(updatedUser.getBusinessHours());

        if (updatedUser.getSocialLink() != null)
            user.setSocialLink(updatedUser.getSocialLink());

        if (updatedUser.getProductsOrServices() != null)
            user.setProductsOrServices(updatedUser.getProductsOrServices());

        return userRepository.save(user);
    }
    public String getSecurityQuestion(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getSecurityQuestion().getQuestion();
    }
    public void resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getSecurityAnswer().equalsIgnoreCase(request.getAnswer())) {
            throw new RuntimeException("Incorrect security answer");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}
