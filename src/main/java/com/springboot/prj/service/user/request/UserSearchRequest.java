package com.springboot.prj.service.user.request;

import com.springboot.lib.constant.RestConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSearchRequest {
    private int pageIndex = RestConstant.PAGE.PAGE_INDEX_DEFAULT;
    private int pageSize = RestConstant.PAGE.PAGE_SIZE_DEFAULT;
    private boolean haveCache = false;
}
