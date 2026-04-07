package com.santiGalarza.order_management.order;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(UUID id) {
        super("Item with id " + id + " not found");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
