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
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(createOrderRequest));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id, @RequestBody @Valid UpdateStatusRequest updateStatusRequest){
        return ResponseEntity.ok(orderService.updateStatus(id,updateStatusRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemResponse>> getItems(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getAllItems(id));
    }

    @GetMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponse> getItemById(
            @PathVariable UUID id, @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.getItemById(id,itemId));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ItemResponse> createItem(
            @PathVariable UUID id, @RequestBody @Valid CreateItemRequest createItemRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createItem(id, createItemRequest));
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponse> updateItemQuantity(
            @PathVariable UUID id, @PathVariable UUID itemId, @RequestBody @Valid PatchItemRequest patchItemRequest){
        return ResponseEntity.ok(orderService.updateItemQuantity(id,itemId,patchItemRequest));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        orderService.deleteItem(id,itemId);
        return ResponseEntity.noContent().build();
    }
}