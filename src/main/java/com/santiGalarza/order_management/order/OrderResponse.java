package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.ItemResponse;
import com.santiGalarza.order_management.order.status.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        LocalDateTime creationDate,
        BigDecimal totalPrice,
        OrderStatus status,
        List<ItemResponse> items
) {}
