package com.springboot.prj.service.user.cache;

import com.springboot.jpa.repository.UserRepository;
import com.springboot.lib.cache.LazyCache;
import com.springboot.prj.service.user.UserMapper;
import com.springboot.prj.service.user.dto.UserDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsersCache extends LazyCache<List<UserDTO>> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UsersCache(UserRepository userRepository, UserMapper userMapper) {
        // Cache 5 phút (5 * 60 * 1000 ms)
        super(5 * 60 * 1000);
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> load() {
        return userRepository.findAll().stream().map(this.userMapper::toDTO).collect(Collectors.toList());
    }
}
