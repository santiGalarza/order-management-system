package com.santiGalarza.order_management.order;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemRequestDto {

    private UUID productId;

    @Min(1)
    private int quantity;
}
