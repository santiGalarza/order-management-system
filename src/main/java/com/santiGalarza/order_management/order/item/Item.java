package com.santiGalarza.order_management.order.item;

import com.santiGalarza.order_management.common.base.Auditable;
import com.santiGalarza.order_management.order.core.Order;
import com.santiGalarza.order_management.product.Product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Positive
    @Digits(integer = 8, fraction = 2)
    @NotNull
    private BigDecimal unitPrice;

    @Min(1)
    private int quantity;

    public static Item of(Order order, Product product, int quantity) {
        Item item = new Item();
        item.order = order;
        item.product = product;
        item.quantity = quantity;
        item.unitPrice = product.getPrice();
        return item;
    }

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