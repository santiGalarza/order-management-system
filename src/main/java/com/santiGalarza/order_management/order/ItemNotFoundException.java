package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.exception.ResourceNotFoundException;

import java.util.UUID;

public class ItemNotFoundException extends ResourceNotFoundException {
    public ItemNotFoundException(UUID id) {
        super("Item with id " + id + " not found");
    }
    public ItemNotFoundException(String message) {
        super(message);
    }
}
