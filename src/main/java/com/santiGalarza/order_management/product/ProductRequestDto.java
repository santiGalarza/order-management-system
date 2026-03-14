package com.santiGalarza.order_management.product;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {

    @NotNull
    private String name;

    @Column(precision = 8, scale = 2)
    @NotNull
    private BigDecimal price;

    @Size(min = 8, max = 12)
    @NotEmpty
    @NotNull
    private String sku;

    private boolean isActive;
    private int minOrderQuantity;
    private int stockQuantity;
    private float weight;
}
