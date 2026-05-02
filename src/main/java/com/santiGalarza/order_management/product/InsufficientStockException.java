package com.santiGalarza.order_management.product;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID id) {
        super("Product with id "+id+" has insufficient stock");
    }
}
