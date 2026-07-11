package com.santiGalarza.order_management.order.item;

import jakarta.validation.constraints.Min;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PatchItemRequest {

    private UUID id;

    @Min(1)
    private Integer quantity;
}
