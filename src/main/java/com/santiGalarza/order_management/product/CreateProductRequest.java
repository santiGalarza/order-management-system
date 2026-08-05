package com.santiGalarza.order_management.product;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {

    @NotEmpty
    private String name;

    @NotNull
    @Digits(integer = 8, fraction = 2)
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
    private BigDecimal weight;

    @NotNull
    private UUID categoryId;
}
