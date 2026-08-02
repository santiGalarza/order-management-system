package com.santiGalarza.order_management.security;

import com.santiGalarza.order_management.security.token.RefreshTokenData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import com.santiGalarza.order_management.user.UserRepository;

import java.time.Duration;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final long refreshExpiration;

    private static final String PREFIX = "refresh:";
    private static final String REVOKED_PREFIX = "revoked:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    public RefreshTokenService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.refreshExpiration = refreshExpiration;
    }

    public String generate(User user, String deviceId) {
        String token = UUID.randomUUID().toString();
        String hashedToken = hash(token);
        Duration ttl = Duration.ofMillis(refreshExpiration);

        RefreshTokenData data = new RefreshTokenData(
                user.getId(),
                user.getTokenVersion(),
                Instant.now(),
                deviceId);

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(PREFIX + hashedToken, json, ttl);
            redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + user.getId(), hashedToken);
            redisTemplate.expire(USER_TOKENS_PREFIX + user.getId(), ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize refresh token data", e);
        }

        return token;
    }

    public RefreshTokenData validate(String rawToken, User user) {
        String hashedToken = hash(rawToken);

        Boolean isRevoked = redisTemplate.hasKey(REVOKED_PREFIX + hashedToken);
        if (Boolean.TRUE.equals(isRevoked)) {
            revokeAllForUser(user.getId());
            throw new RefreshTokenReusedException();
        }

        String json = redisTemplate.opsForValue().get(PREFIX + hashedToken);
        if (json == null) {
            throw new InvalidRefreshTokenException();
        }

        try {
            RefreshTokenData data = objectMapper.readValue(json, RefreshTokenData.class);

            if (data.tokenVersion() != user.getTokenVersion()) {
                revoke(rawToken, user.getId());
                throw new InvalidRefreshTokenException();
            }

            return data;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize refresh token data", e);
        }
    }

    public void revoke(String rawToken, UUID userId) {
        String hashedToken = hash(rawToken);
        String json = redisTemplate.opsForValue().get(PREFIX + hashedToken);

        if (json != null) {
            redisTemplate.opsForValue().set(
                    REVOKED_PREFIX + hashedToken,
                    json,
                    Duration.ofMillis(refreshExpiration));
            redisTemplate.delete(PREFIX + hashedToken);
            redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + userId, hashedToken);
        }
    }

    public void revokeAllForUser(UUID userId) {
        Set<String> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);
        if (tokens != null) {
            tokens.forEach(hashedToken -> {
                String json = redisTemplate.opsForValue().get(PREFIX + hashedToken);
                if (json != null) {
                    redisTemplate.opsForValue().set(
                            REVOKED_PREFIX + hashedToken,
                            json,
                            Duration.ofMillis(refreshExpiration));
                    redisTemplate.delete(PREFIX + hashedToken);
                }
            });
        }
        redisTemplate.delete(USER_TOKENS_PREFIX + userId.toString());
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
