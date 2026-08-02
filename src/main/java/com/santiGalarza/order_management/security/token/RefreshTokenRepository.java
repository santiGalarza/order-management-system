package com.santiGalarza.order_management.security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class RefreshTokenRepository {
    private static final String PREFIX = "refresh:";
    private static final String REVOKED_PREFIX = "revoked:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final TokenHasher tokenHasher;
    private final Duration ttl;

    public RefreshTokenRepository(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            TokenHasher tokenHasher,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.tokenHasher = tokenHasher;
        this.ttl = Duration.ofMillis(refreshExpiration);
    }

    public void save(String rawToken, RefreshTokenData data) {
        String hashed = tokenHasher.hash(rawToken);
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(PREFIX + hashed, json, ttl);
            redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + data.userId(), hashed);
            redisTemplate.expire(USER_TOKENS_PREFIX + data.userId(), ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize refresh token", e);
        }
    }

    public Optional<RefreshTokenData> find(String rawToken) {
        String json = redisTemplate.opsForValue().get(PREFIX + tokenHasher.hash(rawToken));
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, RefreshTokenData.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize refresh token", e);
        }
    }

    public boolean isRevoked(String rawToken) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(REVOKED_PREFIX + tokenHasher.hash(rawToken)));
    }

    public Optional<RefreshTokenData> findRevoked(String rawToken) {
        String json = redisTemplate.opsForValue().get(REVOKED_PREFIX + tokenHasher.hash(rawToken));
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, RefreshTokenData.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize revoked token", e);
        }
    }

    public void revoke(String rawToken) {
        String hashed = tokenHasher.hash(rawToken);
        String json = redisTemplate.opsForValue().get(PREFIX + hashed);
        if (json != null) {
            redisTemplate.opsForValue().set(REVOKED_PREFIX + hashed, json, ttl);
            redisTemplate.delete(PREFIX + hashed);
        }
    }

    public void removeFromUserSet(String rawToken, UUID userId) {
        redisTemplate.opsForSet().remove(
                USER_TOKENS_PREFIX + userId,
                tokenHasher.hash(rawToken));
    }

    public Set<String> findAllHashedTokensForUser(UUID userId) {
        Set<String> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);
        return tokens != null ? tokens : Set.of();
    }

    public void deleteUserTokenSet(UUID userId) {
        redisTemplate.delete(USER_TOKENS_PREFIX + userId.toString());
    }

    public void revokeAllForUser(UUID userId) {
        findAllHashedTokensForUser(userId).forEach(hashed -> {
            String json = redisTemplate.opsForValue().get(PREFIX + hashed);
            if (json != null) {
                redisTemplate.opsForValue().set(REVOKED_PREFIX + hashed, json, ttl);
                redisTemplate.delete(PREFIX + hashed);
            }
        });
        deleteUserTokenSet(userId);
    }
}
