package com.santiGalarza.order_management.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProductRequest {

    @NotEmpty
    private String name;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @NotNull
    private Boolean isActive;

    @NotNull
    @Positive
    private Integer minOrderQuantity;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @NotNull
    @Positive
    private Float weight;

    @NotNull
    private UUID categoryId;
}
