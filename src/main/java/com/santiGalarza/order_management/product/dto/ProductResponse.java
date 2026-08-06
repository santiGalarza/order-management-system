package com.santiGalarza.order_management.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        String sku,
        UUID categoryId,
        String categoryName,
        boolean isActive,
        int minOrderQuantity,
        int stockQuantity,
        float weight,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
