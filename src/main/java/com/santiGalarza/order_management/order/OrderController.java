package com.santiGalarza.order_management.order;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateStatus(
            @PathVariable UUID id, @RequestBody @Valid UpdateStatusRequest dto){
        return ResponseEntity.ok(orderService.updateStatus(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemResponseDto>> getItems(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getAllItems(id));
    }

    @GetMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(
            @PathVariable UUID id, @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.getItemById(id,itemId));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ItemResponseDto> createItem(
            @PathVariable UUID id, @RequestBody @Valid ItemRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createItem(id, dto));
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItemQuantity(
            @PathVariable UUID id, @PathVariable UUID itemId, @RequestBody @Valid ItemUpdateRequestDto dto){
        return ResponseEntity.ok(orderService.updateItemQuantity(id,itemId,dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        orderService.deleteItem(id,itemId);
        return ResponseEntity.noContent().build();
    }
}