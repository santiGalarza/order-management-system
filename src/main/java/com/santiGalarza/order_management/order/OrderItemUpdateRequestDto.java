package com.santiGalarza.order_management.order;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemUpdateRequestDto {
    @Min(1)
    private Integer quantity;
}
