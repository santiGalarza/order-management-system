package com.santiGalarza.order_management.order.item.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        int quantity
) {
}
