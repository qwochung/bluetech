package com.example.bluetech.config;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.respone.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String MESSAGE_UNAUTHORIZED = "Unauthorized";
    private static final String MESSAGE_FORBIDDEN = "Forbidden";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        int statusCode = errorCode.getCode();
        String errorMessage = errorCode.getMessage();

        if (authException instanceof InsufficientAuthenticationException) {
            statusCode = HttpServletResponse.SC_FORBIDDEN;
            ErrorCode errorCodeAccessDenied = ErrorCode.ACCESS_DENIED;
            errorMessage = errorCodeAccessDenied.getMessage();
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);


        Response apiResponse = new Response();
        apiResponse.setCode(statusCode);
        apiResponse.setStatus(HttpStatus.valueOf(statusCode).getReasonPhrase());
        apiResponse.setMessage(errorMessage);
        apiResponse.setData(null);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}