package com.santiGalarza.order_management.order.exception;

import com.santiGalarza.order_management.common.exception.ConflictException;

import java.util.UUID;

public class OrderNotModifiableException extends ConflictException {
    public OrderNotModifiableException(UUID id) {
        super("Order with id " + id + " cannot be modified in its current status");
    }

    @Override
    public String getTitle() {
        return "Order Not Modifiable";
    }
}
