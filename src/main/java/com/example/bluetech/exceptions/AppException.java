package com.example.bluetech.exceptions;

import com.example.bluetech.constant.ErrorCode;
import lombok.Getter;


@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


}
