package com.springboot.prj.service.user.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private Timestamp registrationDate;
    private Timestamp lastLoginDate;
    private long roleGroupId;
}
