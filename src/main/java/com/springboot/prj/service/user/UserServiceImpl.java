package com.springboot.prj.service.user;

import com.springboot.jpa.domain.User;
import com.springboot.jpa.repository.UserRepository;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.enums.EStatus;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.service.redis.Redis;
import com.springboot.prj.service.user.response.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.CustomLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@CustomLog
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final UserMapper userMapper;
    private final Redis redis;

    public UserServiceImpl(UserRepository userRepository, EntityManager entityManager, UserMapper userMapper, Redis redis) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.redis = redis;
    }

    @Override
    public List<UserDTO> getUsers(PagingData pagingData) {
        Pageable pageable = PageRequest.of(pagingData.getPageIndex(), pagingData.getPageSize());
        Page<User> userPage = userRepository.findByStatus(EStatus.ACTIVE.getValue(), pageable);
        pagingData.update(userPage);

        return userPage.stream().map(this.userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUser(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new AppException(ErrorCodes.USER.NOT_FOUND);
        };
        return this.userMapper.toDTO(userOptional.get());
    }

    @Override
    @Transactional
    public UserDTO create(UserRequest userRequest) {
        if (!redis.singleRequest(userRequest.getUsername(), 3)){
            throw new AppException(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        User user = this.userMapper.toEntity(userRequest);
        // user = this.userRepository.save(user);
        this.entityManager.persist(user);
        log.info("userID: " + user.getId());
        this.testEM(user);
        log.info("user: " + user);
        testCache(user);
        // Khi cần
        this.entityManager.flush();
        this.entityManager.clear();
        return this.userMapper.toDTO(user);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void testEM(User user) {
        user.setUsername("ducnguyenTestNewTransaction");
        this.entityManager.persist(user);
    }

    @Override
    public UserDTO update(UserRequest userRequest, long userId) {
        if (!redis.singleRequest(String.valueOf(userId), 3)){
            throw new AppException(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        userRequest.setId(userId);
        User user = this.userMapper.toEntity(userRequest);
        user = this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }

    @Override
    public UserDTO delete(long userId) {
        if (!redis.singleRequest(String.valueOf(userId), 3)){
            throw new AppException(ErrorCodes.SYSTEM.DUPLICATE_REQUEST);
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        User user = userOptional.get();
        user.setStatus(EStatus.DELETED.getValue());
        user = this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }

    /**
     * Trong cùng 1 transaction khi query lần 2 trở đi sẽ lấy data từ persistence context
     */
    @Transactional
     private void testCache(User user) {
        long start, end;

        System.out.println("=== Case 1: findById lần 1 (DB hit) ===");
        start = System.currentTimeMillis();
        User u1 = userRepository.findById(user.getId()).orElse(null);
        end = System.currentTimeMillis();
        System.out.println("u1 = " + u1);
        System.out.println("Time: " + (end - start) + " ms\n");

        System.out.println("=== Case 2: findById lần 2 trong cùng transaction (cache hit) ===");
        start = System.currentTimeMillis();
        User u2 = userRepository.findById(user.getId()).orElse(null);
        end = System.currentTimeMillis();
        System.out.println("u2 = " + u2);
        System.out.println("Time: " + (end - start) + " ms\n");

        start = System.currentTimeMillis();
        User u3 = userRepository.findById(user.getId()).orElse(null);
        end = System.currentTimeMillis();
        System.out.println("u3 = " + u3);
        System.out.println("Time: " + (end - start) + " ms\n");

        System.out.println("=== Case 4: findAll lần 1 (DB hit) ===");
        start = System.currentTimeMillis();
        List<User> all1 = userRepository.findAll();
        end = System.currentTimeMillis();
        System.out.println("all1.size = " + all1.size());
        System.out.println("Time: " + (end - start) + " ms\n");

        System.out.println("=== Case 5: findAll lần 2 trong cùng transaction (cache hit) ===");
        start = System.currentTimeMillis();
        List<User> all2 = userRepository.findAll();
        end = System.currentTimeMillis();
        System.out.println("all2.size = " + all2.size());
        System.out.println("Time: " + (end - start) + " ms\n");
    }
}
