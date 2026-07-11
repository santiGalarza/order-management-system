package com.santiGalarza.order_management.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Positive
    private int minOrderQuantity;

    @PositiveOrZero
    private int stockQuantity;

    @Positive
    private float weight;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean isActive;

    public void deductStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new InsufficientStockException(this.id);
        }
        this.stockQuantity -= quantity;

        /*
        TODO: implement alerts when stock quantity under threshold

        if (stockQuantity <= threshold){
        }
         */
    }

    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
