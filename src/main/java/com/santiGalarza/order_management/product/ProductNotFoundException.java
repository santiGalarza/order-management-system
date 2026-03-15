package com.santiGalarza.order_management.product;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(UUID id){
        super("Product with id " + id + " not found");
    }
}
