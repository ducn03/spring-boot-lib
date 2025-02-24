package com.springboot.lib.exception;

public class AppThrower {
    public static void ep(int errorCode) {
        throw new AppException(errorCode);
    }
}
