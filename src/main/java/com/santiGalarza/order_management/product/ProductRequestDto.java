package com.santiGalarza.order_management.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Integer minOrderQuantity;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    private Float weight;
}
