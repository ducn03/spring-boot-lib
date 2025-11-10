package com.springboot.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "U_USER")
@ToString
public class User extends BaseEntity {
    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "registration_date")
    private Timestamp registrationDate;

    @Column(name = "last_login_date")
    private Timestamp lastLoginDate;

    @Column(name = "role_group_id")
    private long roleGroupId;

}
