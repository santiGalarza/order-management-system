package com.santiGalarza.order_management.order.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order,UUID> {
    List<Order> findByUserId(UUID userId);
    Optional<Order> findByIdAndUser_Email(UUID id, String email);
}
