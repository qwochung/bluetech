package com.example.bluetech.service;

import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.entity.User;

import java.util.Map;

public interface AuthService {
    User register(RegisterRequest request);
    Map<String, Object> login(Map<String, String> request);
    void handleForgotPassword(String email);

    void sendVerifyAccount(String email);
    Map<String, Object> verifyAccount(String token, String id);
}
