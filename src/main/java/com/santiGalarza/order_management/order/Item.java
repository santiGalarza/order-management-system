package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.product.Product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Positive
    @Digits(integer = 8, fraction = 2)
    private BigDecimal unitPrice;

    @Min(1)
    private int quantity;

    public void updateQuantity(int newQuantity) {
        int delta = newQuantity - this.quantity;
        if (delta > 0) {
            this.product.deductStock(delta);
        } else if (delta < 0) {
            this.product.restoreStock(-delta);
        }
        this.quantity = newQuantity;
    }
}