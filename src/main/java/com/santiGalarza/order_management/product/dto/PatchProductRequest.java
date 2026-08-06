package com.santiGalarza.order_management.product.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PatchProductRequest {

    private String name;

    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    private Boolean isActive;

    @Positive
    private Integer minOrderQuantity;

    @PositiveOrZero
    private Integer stockQuantity;

    @Positive
    private Float weight;

    private UUID categoryId;
}
