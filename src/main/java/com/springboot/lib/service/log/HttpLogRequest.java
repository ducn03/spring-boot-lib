package com.springboot.lib.service.log;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HttpLogRequest {
    private Long id;
    private String ip;
    private String method;
    private String url;
    private String targetMethod;
    private String headers;
    private String body;
    private String args;

    private int statusCode;
    private long duration;
    private String result;
}
