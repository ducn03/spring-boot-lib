package com.springboot.lib.exception;

public interface ErrorCodes {
    int OK = 200;

    interface SYSTEM {
        int BAD_REQUEST = 400;
        int BAD_GATEWAY = 502;
        int UNAUTHORIZED = 401;

        int PAGE_NOT_FOUND = 404;
        int DUPLICATE_REQUEST = 40000001;
    }
}
