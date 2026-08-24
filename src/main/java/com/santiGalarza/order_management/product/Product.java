package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.category.Category;
import com.santiGalarza.order_management.common.base.Auditable;
import com.santiGalarza.order_management.product.exception.InsufficientStockException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @NotNull
    private String name;

    @Column(precision = 8, scale = 2)
    @NotNull
    private BigDecimal price;

    @Column(unique = true, nullable = false, updatable = false)
    @Size(min = 8, max = 12)
    @NotEmpty
    private String sku;

    @Column(nullable = false)
    @Positive
    private int minOrderQuantity;

    @Column(nullable = false)
    @PositiveOrZero
    private int stockQuantity;

    @Column(nullable = false)
    @Positive
    private float weight;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

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

    public static Product create(String name, BigDecimal price, String sku, int minOrderQuantity,
                                 int stockQuantity, float weight, Category category, boolean isActive) {
        Product product = new Product();
        product.name = name;
        product.price = price;
        product.sku = sku;
        product.minOrderQuantity = minOrderQuantity;
        product.stockQuantity = stockQuantity;
        product.weight = weight;
        product.category = category;
        product.isActive = isActive;
        return product;
    }
}
