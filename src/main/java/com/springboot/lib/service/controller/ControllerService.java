package com.springboot.lib.service.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.function.ServerResponse;

public interface ControllerService {
    ServerResponse success(Object data);
    <T> ServerResponse success(Object data, Page<T> page);

    ServerResponse error(int error, String message);

    ServerResponse systemError();
}
