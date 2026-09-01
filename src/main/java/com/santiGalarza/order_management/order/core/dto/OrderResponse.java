package com.santiGalarza.order_management.order.core.dto;

import com.santiGalarza.order_management.order.item.dto.ItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        LocalDateTime creationDate,
        BigDecimal totalPrice,
        String statusCode,
        String statusLabel,
        List<ItemResponse> items
) {}
