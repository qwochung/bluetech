package com.example.bluetech.exceptions;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<Response> handleAppException(AppException e) {
        Response response = new Response();
        ErrorCode errorCode = e.getErrorCode();
        response.setCode(e.getErrorCode().getCode());
        response.setMessage(e.getMessage());
        response.setStatus(errorCode.getHttpStatus().getReasonPhrase());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }





//    @ExceptionHandler(value = AccessDeniedException.class)
//    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException e) {
//        Response response = new Response();
//        response.setCode(HttpStatus.FORBIDDEN.value());
//        response.setMessage(e.getMessage());
//        response.setStatus(HttpStatus.FORBIDDEN.toString());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//    }









    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Response> handleRuntimeException(RuntimeException e) {
        Response response = new Response();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}