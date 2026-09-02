package com.santiGalarza.order_management.order.status;

import com.santiGalarza.order_management.common.base.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_statuses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatus extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String label;

    private boolean isInitial;
    private boolean isFinal;
    private boolean isModifiable;

    @ElementCollection
    @CollectionTable(name = "order_status_metadata", joinColumns = @JoinColumn(name = "status_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;

    public String getMetadata(String key) {
        return metadata != null ? metadata.get(key) : null;
    }

    public static OrderStatus create(String code, String label, boolean isInitial, boolean isFinal, boolean isModifiable) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCode(code);
        orderStatus.setLabel(label);
        orderStatus.setInitial(isInitial);
        orderStatus.setFinal(isFinal);
        orderStatus.setModifiable(isModifiable);
        return orderStatus;
    }
}