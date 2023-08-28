package com.tenius.sns.repository;

public interface TokenBlacklistRepository {
    void save(String key, String value, long expiration);
    boolean exists(String key);
}
