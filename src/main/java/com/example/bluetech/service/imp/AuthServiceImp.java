package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.UserRepository;
import com.example.bluetech.security.JwtService;
import com.example.bluetech.service.AuthService;
import com.example.bluetech.service.EmailService;
import com.example.bluetech.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImp implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    @Value("${domain}")
    private String domain;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreatedAt(System.currentTimeMillis());
        user.setHasVerified(false);
        user.setStatus(Status.PENDING);
        return userRepository.save(user);
    }

    @Override
    public Map<String, Object> login(Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            throw new AppException(ErrorCode.MISSING_REQUIRE_PARAM);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (!user.isHasVerified() && user.getStatus() != Status.ACTIVE) {
                throw new AppException(ErrorCode.ACCOUNT_NOT_VERIFIED);
            }

            String accessToken = jwtService.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", accessToken);
            response.put("user", user);

            return response;
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        } catch (Exception e) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
    }

    @Override
    public void handleForgotPassword(String email) {

    }

    @Override
    public void sendVerifyAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String token = UUID.randomUUID().toString();
        userService.updateVerificationToken(token, user.getId());

        String link = domain + "/auth/verify-account/confirmation?token=" + token + "&id=" + user.getId();
        log.info("link: {}", link);

        emailService.sendMailWithLink(email, "Confirm Registration", "confirmRegistration", user.getUserName(), link);

    }

    @Override
    public Map<String, Object> verifyAccount(String token, String id) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || id == null) {
            response.put("success", false);
            response.put("message", "Invalid request");
            return response;
        }

        User user = userService.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (token.equals(user.getVerificationToken())) {
            user.setVerificationToken(null);
            user.setHasVerified(true);
            user.setStatus(Status.ACTIVE);
            userService.save(user);
            log.info("Account verification successful");
            response.put("success", true);
            response.put("message", "Account has been successfully verified");
        } else {
            log.warn("Invalid verification token");
            response.put("success", false);
            response.put("message", "Invalid verification token");
        }

        return response;
    }

}
