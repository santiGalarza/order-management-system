package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.common.base.Auditable;
import com.santiGalarza.order_management.order.core.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_status_history")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "from_status_id")
    private OrderStatus fromStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_status_id")
    private OrderStatus toStatus;

    @Column(name = "changed_by")
    private UUID changedBy;

    private String notes;

    public static OrderStatusHistory of(Order order, OrderStatus from, OrderStatus to, UUID changedBy, String notes) {
        OrderStatusHistory h = new OrderStatusHistory();
        h.order = order;
        h.fromStatus = from;
        h.toStatus = to;
        h.changedBy = changedBy;
        h.notes = notes;
        return h;
    }
}
