package com.santiGalarza.order_management.category;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Category with id " + id + " not found");
    }
}
