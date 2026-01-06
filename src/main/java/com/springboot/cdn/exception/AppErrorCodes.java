package com.springboot.cdn.exception;

import com.springboot.lib.exception.ErrorCodes;

public interface AppErrorCodes extends ErrorCodes {

    interface CDN {
        int CDN_NOT_FOUND = 10003114;
    }

    interface FILE {
        int FILE_NOT_FOUND = 10004114;
        int FILE_INIT_ERROR = 10004115;
        int FILE_EMPTY = 10004116;
        int FILE_UPLOAD_ERROR = 10004117;
        int FILE_INVALID_URL = 10004118;
    }
}
