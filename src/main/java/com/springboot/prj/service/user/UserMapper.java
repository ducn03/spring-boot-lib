package com.springboot.prj.service.user;

import com.springboot.jpa.domain.User;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;

public class UserMapper {
    public User toEntity(UserRequest input){
        User output = new User();

        output.setId(input.getId());
        output.setUsername(input.getUsername());
        output.setPassword(input.getPassword());
        output.setEmail(input.getEmail());
        output.setPhone(input.getPhone());
        output.setFullName(input.getFullName());
        output.setRegistrationDate(input.getRegistrationDate());
        output.setLastLoginDate(input.getLastLoginDate());
        output.setRoleGroupId(input.getRoleGroupId());

        return output;
    }

    public UserDTO toDTO(User input){
        UserDTO output = new UserDTO();

        output.setId(input.getId());
        output.setUsername(input.getUsername());
        output.setEmail(input.getEmail());
        output.setPhone(input.getPhone());
        output.setFullName(input.getFullName());
        output.setRegistrationDate(input.getRegistrationDate());
        output.setLastLoginDate(input.getLastLoginDate());
        output.setRoleGroupId(input.getRoleGroupId());

        return output;
    }
}
