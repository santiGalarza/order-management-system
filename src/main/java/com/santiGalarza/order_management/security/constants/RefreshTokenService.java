package com.santiGalarza.order_management.security.constants;

import com.santiGalarza.order_management.security.token.RefreshTokenData;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.security.token.RefreshTokenRepository;
import com.santiGalarza.order_management.security.InvalidRefreshTokenException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository tokenRepository;
    private final RefreshTokenReusePolicy reusePolicy;

    public RefreshTokenService(RefreshTokenRepository tokenRepository, RefreshTokenReusePolicy reusePolicy) {
        this.tokenRepository = tokenRepository;
        this.reusePolicy = reusePolicy;
    }

    public String generate(User user, String deviceId){
        String token = UUID.randomUUID().toString();
        tokenRepository.save(token, new RefreshTokenData(
                user.getId(),
                user.getTokenVersion(),
                Instant.now(),
                deviceId));
        return token;
    }

    public RefreshTokenData validate(String rawToken, User user){
        reusePolicy.enforce(rawToken);

        RefreshTokenData data = tokenRepository.find(rawToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        if(data.tokenVersion() != user.getTokenVersion()) {
            tokenRepository.revoke(rawToken);
            tokenRepository.removeFromUserSet(rawToken, user.getId());
            throw new InvalidRefreshTokenException();
        }

        return data;
    }

    public void revoke(String rawToken, UUID userId) {
        tokenRepository.revoke(rawToken);
        tokenRepository.removeFromUserSet(rawToken, userId);
    }

    public void revokeAllForUser(UUID userId) {
        tokenRepository.revokeAllForUser(userId);
    }

    public RefreshTokenData peek(String rawToken) {
        reusePolicy.enforce(rawToken);
        return tokenRepository.find(rawToken)
                .orElseThrow(InvalidRefreshTokenException::new);
    }
}
