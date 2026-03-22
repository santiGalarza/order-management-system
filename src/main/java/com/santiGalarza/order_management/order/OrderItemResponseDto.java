package com.santiGalarza.order_management.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDto(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        int quantity
) {
}
