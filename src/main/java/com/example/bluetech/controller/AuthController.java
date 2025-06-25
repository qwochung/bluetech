package com.example.bluetech.controller;

import com.example.bluetech.config.JwtConfig;
import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.request.RegisterRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.AuthService;
import com.example.bluetech.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtConfig jwtConfig;
    @Value("${refresh-expiration}")
    private long maxAge;

    @PostMapping("/register")
    public Response register(@RequestBody RegisterRequest registerRequest) {
        return Response.builder(authService.register(registerRequest)).build();
    }

    @PostMapping("/login")
    public Response login(@RequestBody Map<String, String> request,
                          HttpServletResponse httpResponse) {
        Map<String, Object> response = authService.login(request);
        User user = (User) response.get("user");
        String refreshToken = jwtConfig.generateToken(user, "refresh");

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

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

    @GetMapping("/refresh-token")
    public Response refreshToken(@CookieValue("refresh_token") String requestRefreshToken) {

        Jwt decodedOldRefreshToken = this.jwtConfig.checkValidRefreshToken(requestRefreshToken);

        String email = decodedOldRefreshToken.getSubject();

        if (email == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = this.jwtConfig.generateToken(user, "access");

        return Response.builder(newAccessToken).build();
    }

}
