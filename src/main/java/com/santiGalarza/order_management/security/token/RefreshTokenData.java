package com.santiGalarza.order_management.security.token;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenData(
        UUID userId,
        int tokenVersion,
        Instant issuedAt,
        String deviceId
) {
}
