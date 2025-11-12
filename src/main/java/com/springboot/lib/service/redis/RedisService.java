package com.springboot.lib.service.redis;

import lombok.CustomLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@CustomLog
public class RedisService implements Redis, RedisConstant {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Boolean> singleRequest;

    public RedisService(RedisTemplate<String, String> redisTemplate,
                        @Qualifier("main-single-request") RedisScript<Boolean> singleRequest) {
        this.redisTemplate = redisTemplate;
        this.singleRequest = singleRequest;
    }


    @Override
    public void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long ttl) {
        this.redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    @Override
    public Long increment(String key) {
        return this.redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void hSet(String key, String hash, String value) {
        this.redisTemplate.opsForHash().put(key, hash, value);
    }

    @Override
    public Set<Object> hGetAll(String key) {
        return this.redisTemplate.opsForHash().keys(key);
    }

    @Override
    public boolean singleRequest(String key, long ttl) {
        // out date
        // return Boolean.TRUE.equals(this.redisTemplate.execute(this.singleRequest, List.of(key, String.valueOf(ttl)), List.of()));
        return this.redisTemplate.opsForValue().setIfAbsent(key, "0", ttl, TimeUnit.SECONDS);
    }

    @Override
    public boolean singleRequestHad(String key, long ttl) {
        // Để chạy benchmark
        return Boolean.TRUE.equals(this.redisTemplate.execute(this.singleRequest, List.of(key, String.valueOf(ttl)), List.of()));
        // return this.redisTemplate.opsForValue().setIfAbsent(key, "0", ttl, TimeUnit.SECONDS);
    }

    @Override
    public boolean hashExists(String key, String hashKey) {
        return this.redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public String hashGet(String key, String hashKey){
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        return value.toString();
    }

    @Override
    public void hashDelete(String key, Object... hashKey) {
        this.redisTemplate.opsForHash().delete(key, hashKey);
    }

}
