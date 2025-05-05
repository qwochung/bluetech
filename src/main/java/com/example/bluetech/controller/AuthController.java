package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.request.LoginRequest;
import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.AuthService;
import com.example.bluetech.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequest registerRequest) {
        return Response.builder(authService.register(registerRequest)).build();
    }

    @PostMapping("/login")
    public Response login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = authService.login(request);
        return Response.builder(response).build();
    }

    @GetMapping("")
    public Response test() {
        return Response.builder("Test oke").build();
    }

    @PostMapping("/verify-account")
    public Response sendMailVerifyAccount(@RequestParam String email) {
        authService.sendVerifyAccount(email);
        Map<String, String> result = Map.of("message", "Please go to email to confirm account!");
        return Response.builder(result).build();
    }

    @GetMapping("/verify-account/confirmation")
    public ModelAndView verifyAccount(
            @RequestParam String token,
            @RequestParam String id
    ) {
        Map<String, Object> result = authService.verifyAccount(token, id);
        boolean isSuccess = (boolean) result.get("success");

        if (isSuccess) {
            return new ModelAndView("comfirmSuccess");
        } else {
            return new ModelAndView("comfirmError");
        }
    }


}
