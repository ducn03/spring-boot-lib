package com.springboot.lib.service.controller;

import com.springboot.lib.dto.PagingData;
import com.springboot.lib.dto.ResponseData;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.JsonHelper;
import lombok.CustomLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@CustomLog
public class ControllerServiceImpl implements ControllerService {
    private final String ERROR_MESSAGE_DEFAULT = "Message chưa được định nghĩa";

    @Override
    public ResponseEntity<?> success(Object data) {
        log.info("response success:");
        log.info(JsonHelper.toJson(data));
        return ResponseEntity.ok().body(ResponseData.success(data));
    }

    @Override
    public ResponseEntity<?> success(Object data, PagingData page) {
        log.info("response success:");
        log.info(JsonHelper.toJson(data));
        return ResponseEntity.ok().body(ResponseData.success(data, page));
    }

    @Override
    public ResponseEntity<?> error(int error, String message) {
        log.info("response error:" + error);
        log.info(message);
        HttpStatus status = switch (error) {
            case ErrorCodes.SYSTEM.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ErrorCodes.SYSTEM.BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case ErrorCodes.SYSTEM.PAGE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.SYSTEM.FORBIDDEN -> HttpStatus.FORBIDDEN;
            case ErrorCodes.SYSTEM.SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case ErrorCodes.SYSTEM.BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.OK;
        };

        return ResponseEntity.status(status).body(ResponseData.error(error, message));
    }

    @Override
    public ResponseEntity<?> error(int error) {
        log.info("response error:" + error);
        HttpStatus status = switch (error) {
            case ErrorCodes.SYSTEM.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ErrorCodes.SYSTEM.BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case ErrorCodes.SYSTEM.PAGE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.SYSTEM.FORBIDDEN -> HttpStatus.FORBIDDEN;
            case ErrorCodes.SYSTEM.SYSTEM_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case ErrorCodes.SYSTEM.BAD_GATEWAY -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.OK;
        };

        return ResponseEntity.status(status).body(ResponseData.error(error, ERROR_MESSAGE_DEFAULT));
    }

    @Override
    public ResponseEntity<?> systemError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseData.error(500, "System Error"));
    }
}
