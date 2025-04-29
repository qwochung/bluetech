package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.request.LoginRequest;
import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.UserRepository;
import com.example.bluetech.security.JwtService;
import com.example.bluetech.service.AuthService;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
}
