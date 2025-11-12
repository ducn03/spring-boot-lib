package com.springboot.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "U_HTTP_LOG")
@ToString
public class HttpLog extends BaseEntity {
    @Column(name = "ip")
    private String ip;

    @Column(name = "method")
    private String method;

    @Column(name = "url")
    private String url;

    @Column(name = "target_method")
    private String targetMethod;

    @Column(name = "headers", length = 1000)
    private String headers;

    @Column(name = "body")
    private String body;

    @Column(name = "args", columnDefinition = "JSON")
    private String args;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "duration")
    private long duration;

    @Column(name = "result", columnDefinition = "JSON")
    private String result;

    @Column(name = "requester")
    private String requester;
}
