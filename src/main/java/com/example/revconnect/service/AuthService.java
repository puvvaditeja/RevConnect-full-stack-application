package com.example.revconnect.service;

import com.example.revconnect.dto.LoginRequest;

public interface AuthService {

    String login(LoginRequest request);

}