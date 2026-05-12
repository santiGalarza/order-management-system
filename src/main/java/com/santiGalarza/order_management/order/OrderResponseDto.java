package com.santiGalarza.order_management.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        LocalDateTime creationDate,
        BigDecimal totalPrice,
        Status status,
        List<ItemResponseDto> items
) {}
