package com.santiGalarza.order_management.order.status;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, UUID> {
    Optional<OrderStatus> findByCode(String code);
    Optional<OrderStatus> findByInitialTrue();
}
