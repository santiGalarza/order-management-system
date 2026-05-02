package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.exception.ResourceNotFoundException;

import java.util.UUID;

public class OrderNotFoundException extends ResourceNotFoundException {
    public OrderNotFoundException(UUID id) {
        super("Order with id " + id + " not found");
    }
}
