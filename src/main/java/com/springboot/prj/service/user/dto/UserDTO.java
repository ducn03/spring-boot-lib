package com.springboot.prj.service.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UserDTO {
    private long id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private Timestamp registrationDate;
    private Timestamp lastLoginDate;
    private long roleGroupId;
}
