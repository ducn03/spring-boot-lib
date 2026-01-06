package com.springboot.cdn.service.cdn.dto;

import com.springboot.cdn.enums.EDomain;
import com.springboot.cdn.enums.EEnv;
import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.enums.EStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class CdnSearchRequest {

    private EEnv env;
    private EStatus status;
    private EDomain domain;
    private String group;

    private Timestamp createdFrom;
    private Timestamp createdTo;

    private Timestamp lastModifiedFrom;
    private Timestamp lastModifiedTo;

    private int pageIndex = RestConstant.PAGE.PAGE_INDEX_DEFAULT;
    private int pageSize = RestConstant.PAGE.PAGE_SIZE_DEFAULT;
}
