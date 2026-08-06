package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.dto.CreateOrderRequest;
import com.santiGalarza.order_management.order.dto.OrderResponse;
import com.santiGalarza.order_management.security.RequiresPermission;
import com.santiGalarza.order_management.order.item.dto.CreateItemRequest;
import com.santiGalarza.order_management.order.item.dto.ItemResponse;
import com.santiGalarza.order_management.order.item.dto.PatchItemRequest;
import com.santiGalarza.order_management.order.status.dto.UpdateStatusRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @RequiresPermission.OrderReadAll
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping("/my")
    @RequiresPermission.OrderRead
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(orderService.getMyOrders(email));
    }

    @GetMapping("/{id}")
    @RequiresPermission.OrderRead
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PostMapping
    @RequiresPermission.OrderCreate
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, email));
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission.StatusManage
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id, @RequestBody @Valid UpdateStatusRequest request){
        return ResponseEntity.ok(orderService.updateStatus(id,request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission.OrderDelete
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/items")
    @RequiresPermission.OrderRead
    public ResponseEntity<List<ItemResponse>> getItems(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getItems(id));
    }

    @GetMapping("/{id}/items/{itemId}")
    @RequiresPermission.OrderRead
    public ResponseEntity<ItemResponse> getItem(
            @PathVariable UUID id, @PathVariable UUID itemId) {
        return ResponseEntity.ok(orderService.getItem(id,itemId));
    }

    @PostMapping("/{id}/items")
    @RequiresPermission.OrderCreate
    public ResponseEntity<ItemResponse> createItem(
            @PathVariable UUID id, @RequestBody @Valid CreateItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createItem(id, request));
    }

    @PatchMapping("/{id}/items/{itemId}")
    @RequiresPermission.OrderUpdate
    public ResponseEntity<ItemResponse> updateItemQuantity(
            @PathVariable UUID id, @PathVariable UUID itemId, @RequestBody @Valid PatchItemRequest request){
        return ResponseEntity.ok(orderService.updateItemQuantity(id,itemId,request));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @RequiresPermission.OrderDelete
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        orderService.deleteItem(id,itemId);
        return ResponseEntity.noContent().build();
    }
}