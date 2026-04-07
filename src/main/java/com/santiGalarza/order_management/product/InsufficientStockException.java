package com.santiGalarza.order_management.product;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID id, int quantity) {
        super("Product with id "+id+"has a total stock of "+quantity);
    }
}
