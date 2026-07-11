package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.CreateItemRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    private List<CreateItemRequest> items;
}
