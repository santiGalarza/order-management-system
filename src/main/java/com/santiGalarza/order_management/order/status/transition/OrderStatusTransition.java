package com.santiGalarza.order_management.order.status.transition;

import com.santiGalarza.order_management.common.base.Auditable;
import com.santiGalarza.order_management.order.status.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "order_status_transitions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"from_status_id","to_status_id"})
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusTransition extends Auditable {

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

    public static OrderStatusTransition create(OrderStatus fromStatus, OrderStatus toStatus, String requiresRole) {
        OrderStatusTransition orderStatusTransition = new OrderStatusTransition();
        orderStatusTransition.setFromStatus(fromStatus);
        orderStatusTransition.setToStatus(toStatus);
        orderStatusTransition.setRequiresRole(requiresRole);
        return orderStatusTransition;
    }
}
