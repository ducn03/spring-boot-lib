package com.springboot.cdn.service.cdn.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import com.springboot.lib.enums.EStatus;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CdnDTO {
    private Long id;
    private Integer stt;

    private EDomain domain;
    private EEnv env;
    private String group;
    private String link;
    private int status;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}
