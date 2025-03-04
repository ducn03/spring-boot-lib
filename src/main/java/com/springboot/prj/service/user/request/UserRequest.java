package com.springboot.prj.service.user.request;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserRequest {
    private long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullName;
    private Timestamp registrationDate;
    private Timestamp lastLoginDate;
    private long roleGroupId;
}
