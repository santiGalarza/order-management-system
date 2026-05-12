package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.exception.ResourceNotFoundException;

import java.util.UUID;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(UUID id) {
        super("Category with id " + id + " not found");
    }
}
