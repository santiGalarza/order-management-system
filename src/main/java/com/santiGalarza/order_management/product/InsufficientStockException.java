package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.common.exception.ConflictException;

import java.util.UUID;

public class InsufficientStockException extends ConflictException {
    public InsufficientStockException(UUID id) {
        super("Product with id "+id+" has insufficient stock");
    }

    @Override
    public String getTitle() {
        return "Insufficient Stock";
    }
}
