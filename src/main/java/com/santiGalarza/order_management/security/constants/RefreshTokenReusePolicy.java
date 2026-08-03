package com.santiGalarza.order_management.security.constants;

import com.santiGalarza.order_management.security.token.RefreshTokenRepository;
import com.santiGalarza.order_management.security.RefreshTokenReusedException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenReusePolicy {

    private final RefreshTokenRepository refreshTokenRepository;
    private static final long GRACE_PERIOD_SECONDS = 5;

    public RefreshTokenReusePolicy(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void enforce(String rawToken) {
        if(!refreshTokenRepository.isRevoked(rawToken)) return;

        refreshTokenRepository.findRevoked(rawToken).ifPresent(data -> {
            boolean withinGrace = data.issuedAt()
                    .plusSeconds(GRACE_PERIOD_SECONDS)
                    .isAfter(Instant.now());
            if(!withinGrace) {
                refreshTokenRepository.revokeAllForUser(data.userId());
            }
        });

        throw new RefreshTokenReusedException();
    }
}
