package com.santiGalarza.order_management.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findByOrderId(UUID orderId);
    List<Item> findByIdInAndOrderId(List<UUID> itemIds, UUID orderId);
    Optional<Item> findByIdAndOrderId(UUID itemId, UUID orderId);
}
