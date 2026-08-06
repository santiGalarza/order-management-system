package com.santiGalarza.order_management.order.dto;

import com.santiGalarza.order_management.order.item.dto.CreateItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    @NotEmpty
    @Valid
    private List<CreateItemRequest> items;
}
