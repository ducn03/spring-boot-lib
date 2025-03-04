package com.springboot.lib.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Meta {
    private int code;
    private String message;
    private String requestId;
    private String responseId;
    private Pagination pagination;

    public Meta() {

    }

    public Meta(int code, String message) {
        this.code = code;
        this.message = message;
        this.requestId = getRequestId();
        this.responseId = generateResponseId();
    }

    private String getRequestId() {
        return UUID.randomUUID().toString();
    }

    private String generateResponseId() {
        return UUID.randomUUID().toString();
    }
}
