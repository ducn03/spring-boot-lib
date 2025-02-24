package com.springboot.lib.service.controller;

import com.springboot.lib.dto.ResponseData;
import com.springboot.lib.helper.JsonHelper;
import lombok.CustomLog;
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
    public ServerResponse error(int error, String message) {
        log.info("response error:" + error);
        log.info(message);
        return ServerResponse.ok().body(ResponseData.error(error, message));
    }

    @Override
    public ServerResponse systemError() {
        return ServerResponse.status(HttpStatus.BAD_GATEWAY).body(ResponseData.error(HttpStatus.BAD_GATEWAY.value(), "System Error"));
    }
}
