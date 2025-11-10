package com.springboot.lib.service.controller;

import com.springboot.lib.dto.PagingData;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.function.ServerResponse;

public interface ControllerService {
    ResponseEntity<?> success(Object data);

    ResponseEntity<?> success(Object data, PagingData pagingData);

    ResponseEntity<?> error(int error, String message);

    ResponseEntity<?> systemError();
}
