package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.common.exception.ResourceNotFoundException;

public class OrderStatusNotFoundException extends ResourceNotFoundException {
    public OrderStatusNotFoundException(String code){
        super("Order status not found: " + code);
    }
}
