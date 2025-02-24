package com.springboot.lib.service.controller;

import org.springframework.web.servlet.function.ServerResponse;

public interface ControllerService {
    ServerResponse success(Object data);

    ServerResponse error(int error, String message);

    ServerResponse systemError();
}
