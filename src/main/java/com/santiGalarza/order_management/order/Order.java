package com.santiGalarza.order_management.order;

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

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @Setter(AccessLevel.NONE)
    private int deliveryAttempts = 0;

    public void updateStatus(Status newStatus, int maxDeliveryAttempts) {
        this.status.validateTransition(newStatus);
        if (newStatus == Status.REATTEMPTING_DELIVERY) {
            incrementDeliveryAttempts(maxDeliveryAttempts);
        }
        this.status = newStatus;
    }

    private void incrementDeliveryAttempts(int maxDeliveryAttempts) {
        if(deliveryAttempts >= maxDeliveryAttempts) {
            throw new MaxDeliveryAttemptsExceededException(this.id);
        }
        deliveryAttempts++;
    }
}
