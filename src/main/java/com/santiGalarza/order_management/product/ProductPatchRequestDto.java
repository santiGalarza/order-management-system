package com.santiGalarza.order_management.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductPatchRequestDto {

    private String name;

    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @Size(min = 8, max = 12)
    private String sku;

    private Boolean isActive;

    @Positive
    private Integer minOrderQuantity;

    @PositiveOrZero
    private Integer stockQuantity;

    @Positive
    private Float weight;
}
