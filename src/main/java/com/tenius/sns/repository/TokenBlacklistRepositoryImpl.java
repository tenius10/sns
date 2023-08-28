package com.tenius.sns.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepositoryImpl implements TokenBlacklistRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String key, String value, long expiration){
        redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }
}
