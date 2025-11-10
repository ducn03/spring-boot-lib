package com.springboot.lib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {
    private int code;
    private String message;
    private String requestId;
    private String responseId;
    private PagingData pagingData;

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
