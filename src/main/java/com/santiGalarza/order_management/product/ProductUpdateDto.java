package com.santiGalarza.order_management.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDto {

    @NotEmpty
    private String name;

    @NotNull
    @Digits(integer = 6, fraction = 2)
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
}
