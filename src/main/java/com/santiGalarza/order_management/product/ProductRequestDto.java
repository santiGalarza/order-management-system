package com.santiGalarza.order_management.product;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {

    @NotEmpty
    private String name;

    @NotNull
    @Digits(integer = 6, fraction = 2)
    private BigDecimal price;

    @NotEmpty
    @Size(min = 8, max = 12)
    private String sku;

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
}
