package com.santiGalarza.order_management.order.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByIdAndOrderId(UUID itemId, UUID orderId);
}
