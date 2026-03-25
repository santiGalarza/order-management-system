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

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder
            (@PathVariable UUID id, @RequestBody @Valid OrderUpdateRequestDto dto){
        return ResponseEntity.ok(orderService.updateOrder(id,dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> patchOrder(
            @PathVariable UUID id, @RequestBody @Valid OrderUpdateRequestDto dto){
        return ResponseEntity.ok(orderService.patchOrder(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponseDto> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}