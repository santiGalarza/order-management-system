package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.CreateItemRequest;
import com.santiGalarza.order_management.order.item.ItemResponse;
import com.santiGalarza.order_management.order.item.PatchItemRequest;
import com.santiGalarza.order_management.order.status.UpdateStatusRequest;
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
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id, @RequestBody @Valid UpdateStatusRequest request){
        return ResponseEntity.ok(orderService.updateStatus(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemResponse>> getItems(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getItems(id));
    }

    @GetMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponse> getItem(
            @PathVariable UUID id, @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.getItem(id,itemId));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ItemResponse> createItem(
            @PathVariable UUID id, @RequestBody @Valid CreateItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createItem(id, request));
    }

    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<ItemResponse> updateItemQuantity(
            @PathVariable UUID id, @PathVariable UUID itemId, @RequestBody @Valid PatchItemRequest request){
        return ResponseEntity.ok(orderService.updateItemQuantity(id,itemId,request));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        orderService.deleteItem(id,itemId);
        return ResponseEntity.noContent().build();
    }
}