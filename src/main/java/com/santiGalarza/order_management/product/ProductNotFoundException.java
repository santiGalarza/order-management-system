package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(UUID id){
        super("Product with id " + id + " not found");
    }
    public ProductNotFoundException(String message) {super(message);}
}
