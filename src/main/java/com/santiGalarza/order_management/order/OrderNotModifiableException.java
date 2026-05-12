package com.santiGalarza.order_management.order;

import java.util.UUID;

public class OrderNotModifiableException extends RuntimeException {
    public OrderNotModifiableException(UUID id) {
        super("Order with id " + id + " cannot be modified in its current status");
    }
}
