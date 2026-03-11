package com.santiGalarza.order_management.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public abstract class ProductRepository implements JpaRepository<Product, UUID> {
}
