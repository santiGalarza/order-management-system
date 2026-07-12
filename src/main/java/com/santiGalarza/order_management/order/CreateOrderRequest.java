package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.CreateItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
