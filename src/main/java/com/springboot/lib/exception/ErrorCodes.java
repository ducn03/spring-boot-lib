package com.springboot.lib.exception;

public interface ErrorCodes {
    int OK = 200;

    interface SYSTEM {
        int BAD_REQUEST = 400;
        int BAD_GATEWAY = 502;
        int UNAUTHORIZED = 401;

        int PAGE_NOT_FOUND = 404;
        int DUPLICATE_REQUEST = 40000001;
        int LOG_ACTIVITY_ERROR = 40000002;
        int FORBIDDEN = 403;
        int SYSTEM_ERROR = 500;
        interface SM{
            int BAD_REQUEST_ACTION_NOT_FOUND = 10001112;
            int BAD_REQUEST_STATE_NOT_FOUND = 10001111;
            int BAD_REQUEST_INPUT_NOT_FOUND = 10001113;
        }
    }
}
