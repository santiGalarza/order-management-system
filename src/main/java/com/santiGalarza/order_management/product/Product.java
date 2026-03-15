package com.santiGalarza.order_management.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private String name;

    @Column(precision = 8, scale = 2)
    @NotNull
    private BigDecimal price;

    @Size(min = 8, max = 12)
    @Column(unique = true)
    @NotEmpty
    @NotNull
    private String sku;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isActive;
    private int minOrderQuantity;
    private int stockQuantity;
    private float weight;

    protected Product(){}
}
