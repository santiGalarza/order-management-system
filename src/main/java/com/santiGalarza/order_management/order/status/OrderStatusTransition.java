package com.santiGalarza.order_management.order.status;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "order_status_transitions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_status_id","to_status_id"})
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_status_id")
    private OrderStatus fromStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_status_id")
    private OrderStatus toStatus;

    @Column(name = "requires_role")
    private String requiresRole;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
