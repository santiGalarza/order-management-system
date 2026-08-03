package com.santiGalarza.order_management.security.constants;

import com.santiGalarza.order_management.security.token.RefreshTokenRepository;
import com.santiGalarza.order_management.security.RefreshTokenReusedException;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenReusePolicy {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenReusePolicy(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void enforce(String rawToken) {
        if(!refreshTokenRepository.isRevoked(rawToken)) return;

        refreshTokenRepository.findRevoked(rawToken).ifPresent(data ->
                refreshTokenRepository.revokeAllForUser(data.userId()));

        throw new RefreshTokenReusedException();
    }
}
