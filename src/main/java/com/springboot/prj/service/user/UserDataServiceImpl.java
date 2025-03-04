package com.springboot.prj.service.user;

import com.springboot.jpa.domain.User;
import com.springboot.jpa.repository.UserRepository;
import com.springboot.lib.enums.Status;
import com.springboot.lib.exception.AppThrower;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.service.redis.Redis;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDataServiceImpl implements UserDataService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Redis redis;

    public UserDataServiceImpl(UserRepository userRepository, UserMapper userMapper, Redis redis) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.redis = redis;
    }

    @Override
    public List<UserDTO> getUsers(Page<User> page) {
        List<User> userList = page.getContent();
        return userList.stream().map(this.userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Page<User> getUsers(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return userRepository.findByStatus(Status.ACTIVE.getValue(), pageable);
    }

    @Override
    public UserDTO getUser(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
        return this.userMapper.toDTO(userOptional.get());
    }

    @Override
    public UserDTO create(UserRequest userRequest) {
        if (!redis.singleRequest(userRequest.getUsername(), 3)){
            AppThrower.ep(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        User user = this.userMapper.toEntity(userRequest);
        this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }

    @Override
    public UserDTO update(UserRequest userRequest, long userId) {
        if (!redis.singleRequest(String.valueOf(userId), 3)){
            AppThrower.ep(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        userRequest.setId(userId);
        User user = this.userMapper.toEntity(userRequest);
        this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }

    @Override
    public UserDTO delete(long userId) {
        if (!redis.singleRequest(String.valueOf(userId), 3)){
            AppThrower.ep(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
        User user = userOptional.get();
        user.setStatus(Status.DELETED.getValue());
        this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }
}
