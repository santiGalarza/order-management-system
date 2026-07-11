package com.santiGalarza.order_management.order.status;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStatusTransitionRepository extends JpaRepository<OrderStatusTransition, UUID> {
    Optional<OrderStatusTransition> findByFromStatusIdAndToStatusId(UUID fromId, UUID toId);
    List<OrderStatusTransition> findByFromStatusId(UUID fromId);
}
