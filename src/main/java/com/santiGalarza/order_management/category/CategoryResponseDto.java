package com.santiGalarza.order_management.category;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name,
        String description,
        CategoryResponseDto parentCategory,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
