package com.example.bluetech.controller;

import com.example.bluetech.dto.request.LoginRequest;
import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequest registerRequest) {
        return Response.builder(authService.register(registerRequest)).build();
    }

    @PostMapping("/login")
    public Response login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = authService.login(request);
        return Response.builder(response).build();
    }

    @PostMapping("/test")
    public Response test(@RequestBody Map<String, String> request) {

        return Response.builder("Test oke").build();
    }
}
