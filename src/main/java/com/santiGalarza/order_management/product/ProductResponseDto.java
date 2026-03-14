package com.santiGalarza.order_management.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        BigDecimal price,
        String sku,
        boolean isActive,
        int minOrderQuantity,
        int stockQuantity,
        float weight,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
