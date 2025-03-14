package constant;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    NOT_FOUND (404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),


    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),
    EMAIL_EXISTED (400, HttpStatus.BAD_REQUEST, "Email already exists"),
    USERNAME_EXISTED (400, HttpStatus.BAD_REQUEST, "Username already exists"),
    INVALID_USERNAME_OR_PASSWORD (400, HttpStatus.BAD_REQUEST, "Invalid user name or password"),
    AUTHENTICATION_FAILED (401, HttpStatus.UNAUTHORIZED, "Authentication failed"),
    ACCESS_DENIED (403, HttpStatus.FORBIDDEN, "Access denied"),
    PERMISSION_NOT_FOUND (404, HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()),
    PERMISSION_ALREADY_EXISTS (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
    ROLE_ALREADY_EXISTS (400, HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()),
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
