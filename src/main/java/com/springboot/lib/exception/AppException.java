package com.springboot.lib.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final int errorCode;
    private String errorMessage;

    public AppException(int errorCode) {
        this.errorCode = errorCode;
    }

    public AppException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public void applyErrorMessage(String message) {
        this.errorMessage = message;
    }

    @Override
    public String getMessage() {
        if (this.errorMessage != null) {
            return this.errorMessage;
        }
        return super.getMessage();
    }
}
