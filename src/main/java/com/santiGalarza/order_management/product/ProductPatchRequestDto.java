package com.santiGalarza.order_management.product;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductPatchRequestDto {

    private String name;

    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @Size(min = 8, max = 12)
    private String sku;

    private Boolean isActive;
    private Integer minOrderQuantity;
    private Integer stockQuantity;
    private Float weight;
}
