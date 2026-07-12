package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.Item;
import com.santiGalarza.order_management.order.status.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Positive
    private BigDecimal totalPrice;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "current_status_id")
    private OrderStatus currentStatus;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    private int deliveryAttempts = 0;

    public void incrementDeliveryAttempts() {
        this.deliveryAttempts++;
    }
}
