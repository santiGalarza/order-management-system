package com.santiGalarza.order_management.order.core;

import com.santiGalarza.order_management.common.base.Auditable;
import com.santiGalarza.order_management.order.item.Item;
import com.santiGalarza.order_management.order.status.OrderStatus;
import com.santiGalarza.order_management.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Positive
    private BigDecimal totalPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "current_status_id")
    private OrderStatus currentStatus;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private int deliveryAttempts = 0;

    public void incrementDeliveryAttempts() {
        this.deliveryAttempts++;
    }

    public void recalculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Order create(BigDecimal totalPrice, OrderStatus currentStatus, List<Item> items, User user) {
        Order order = new Order();
        order.totalPrice = totalPrice;
        order.currentStatus = currentStatus;
        order.items = items;
        order.user = user;
        return order;
    }
}
