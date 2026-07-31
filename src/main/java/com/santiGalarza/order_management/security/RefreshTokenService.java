package com.santiGalarza.order_management.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private final long refreshExpiration;

    private static final String PREFIX = "refresh:";

    public RefreshTokenService(
            StringRedisTemplate redisTemplate,
            @Value("${jwt.refresh.expiration}") long refreshExpiration) {
        this.redisTemplate = redisTemplate;
        this.refreshExpiration = refreshExpiration;
    }

    public String generate(String email) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                PREFIX + token,
                email,
                Duration.ofMillis(refreshExpiration));
        return token;
    }

    public String validate(String token) {
        String email = redisTemplate.opsForValue().get(PREFIX + token);
        if (email == null) {
            throw new InvalidRefreshTokenException();
        }
        return email;
    }

    public void revoke(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
