package com.springboot.lib.service.controller;

import com.springboot.lib.dto.ResponseData;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.JsonHelper;
import lombok.CustomLog;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.ServerResponse;

@Service
@CustomLog
public class ControllerServiceImpl implements ControllerService {

    @Override
    public ServerResponse success(Object data) {
        log.info("response success:");
        log.info(JsonHelper.toJson(data));
        return ServerResponse.ok().body(ResponseData.success(data));
    }

    @Override
    public <T> ServerResponse success(Object data, Page<T> page) {
        log.info("response success:");
        log.info(JsonHelper.toJson(data));
        return ServerResponse.ok().body(ResponseData.success(data, page));
    }

    @Override
    public ServerResponse error(int error, String message) {
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

        return ServerResponse.status(status).body(ResponseData.error(error, message));
    }

    @Override
    public ServerResponse systemError() {
        return ServerResponse.status(HttpStatus.BAD_GATEWAY).body(ResponseData.error(HttpStatus.BAD_GATEWAY.value(), "System Error"));
    }
}
