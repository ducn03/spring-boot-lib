package com.springboot.lib.service.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpLogResponse {
    private long id;
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
