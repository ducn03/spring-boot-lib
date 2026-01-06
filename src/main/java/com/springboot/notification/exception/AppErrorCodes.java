package com.springboot.notification.exception;

import com.springboot.lib.exception.ErrorCodes;

public interface AppErrorCodes extends ErrorCodes {
    interface NOTIFY {
        int PHONE_IS_EMPTY = 10003111;
        int INVALID_MESSAGE_FORMAT = 10003112;
    }
}
