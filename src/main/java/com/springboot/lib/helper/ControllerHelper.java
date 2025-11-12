package com.springboot.lib.helper;

import com.springboot.lib.dto.PagingData;
import com.springboot.lib.dto.ResponseData;
import com.springboot.lib.exception.ErrorCodes;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

public class ControllerHelper {
    private static final String ERROR_MESSAGE_DEFAULT = "Message chưa được định nghĩa";

    public static ResponseEntity<?> success(Object data) {
        return ResponseEntity.ok().body(ResponseData.success(data));
    }

    public static ResponseEntity<?> success(Object data, PagingData page) {
        return ResponseEntity.ok().body(ResponseData.success(data, page));
    }

    public static ResponseEntity<?> error(int error, String message) {
        return ResponseEntity.status(getStatusCode(error))
                             .body(ResponseData
                             .error(error, message));
    }

    public static ResponseEntity<?> error(int error) {
        return ResponseEntity.status(getStatusCode(error))
                             .body(ResponseData
                             .error(error, ERROR_MESSAGE_DEFAULT));
    }

    public static ResponseEntity<?> systemError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ResponseData
                             .error(500, "System Error"));
    }

    private static HttpStatus getStatusCode(int error) {
        return switch (error) {
            case ErrorCodes.SYSTEM.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ErrorCodes.SYSTEM.BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case ErrorCodes.SYSTEM.PAGE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.SYSTEM.FORBIDDEN -> HttpStatus.FORBIDDEN;
            case ErrorCodes.SYSTEM.SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case ErrorCodes.SYSTEM.BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.OK;
        };
    }
}
