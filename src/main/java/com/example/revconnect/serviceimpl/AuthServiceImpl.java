package com.example.revconnect.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.revconnect.dto.LoginRequest;
import com.example.revconnect.entity.User;
import com.example.revconnect.exception.BadRequestException;
import com.example.revconnect.repository.UserRepository;
import com.example.revconnect.security.JwtUtil;
import com.example.revconnect.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService{

    private JwtUtil jwtUtil;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    
    public AuthServiceImpl(JwtUtil theJwtUtil, UserRepository theUserRepository, 
    		PasswordEncoder thePasswordEncoder) {
    	
    	this.jwtUtil = theJwtUtil;
    	this.userRepository = theUserRepository;
    	this.passwordEncoder = thePasswordEncoder;
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email"));

        // secure password check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        // generate token with role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return token;
    }
}
