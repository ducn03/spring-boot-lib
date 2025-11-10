package com.springboot.lib.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final int errorCode;
    private String errorMessage;

    public AppException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AppException(int errorCode) {
        this.errorCode = errorCode;
    }
}
