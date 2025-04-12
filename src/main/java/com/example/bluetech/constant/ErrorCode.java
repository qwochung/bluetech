package com.example.bluetech.constant;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

//     General

    BAD_REQUEST (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    NOT_FOUND (404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),



//    Auth
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),
    EMAIL_EXISTED (400, HttpStatus.BAD_REQUEST, "Email already exists"),
    USERNAME_EXISTED (400, HttpStatus.BAD_REQUEST, "Username already exists"),
    INVALID_USERNAME_OR_PASSWORD (400, HttpStatus.BAD_REQUEST, "Invalid user name or password"),
    AUTHENTICATION_FAILED (401, HttpStatus.UNAUTHORIZED, "Authentication failed"),
    ACCESS_DENIED (403, HttpStatus.FORBIDDEN, "Access denied"),
    PERMISSION_NOT_FOUND (404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),
    PERMISSION_ALREADY_EXISTS (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    ROLE_ALREADY_EXISTS (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    PASSWORD_NOT_MATCH (400, HttpStatus.BAD_REQUEST, "Password and confirm password do not match"),
    UNAUTHORIZED(401,  HttpStatus.UNAUTHORIZED, "Unauthorized"),
    INVALID_PASSWORD(400, HttpStatus.BAD_REQUEST, "Invalid Password!"),
    INVALID_CREDENTIALS(400, HttpStatus.BAD_REQUEST, "Invalid Credentials!"),


//    File
    FILE_SIZE_EXCEEDED (500, HttpStatus.INTERNAL_SERVER_ERROR, "File exceeded"),
    FILE_EXTENSION_NOT_SUPPORTED (500, HttpStatus.INTERNAL_SERVER_ERROR, "File extension not supported"),
    MISSING_REQUIRE_PARAM(400, HttpStatus.BAD_REQUEST, "Missing Require Param!"),



    ;


    private int code;
    private HttpStatus httpStatus;
    private String message;
    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
