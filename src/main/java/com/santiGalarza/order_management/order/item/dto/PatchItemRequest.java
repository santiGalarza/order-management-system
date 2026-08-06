package com.santiGalarza.order_management.order.item.dto;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchItemRequest {

    @Min(1)
    @NotNull
    private Integer quantity;
}
