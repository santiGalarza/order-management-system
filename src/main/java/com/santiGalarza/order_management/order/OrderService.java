package com.santiGalarza.order_management.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    public OrderService(OrderMapper orderMapper, OrderRepository orderRepository) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(){
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto){
        Order order = orderMapper.toEntity(dto);

        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID id, OrderUpdateRequestDto dto){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        Order updatedOrder = orderMapper.updateEntityFromPatchRequestDto(dto,order);
        orderRepository.save(updatedOrder);

        return orderMapper.toResponseDto(updatedOrder);
    }

    @Transactional
    public OrderResponseDto patchOrder(UUID id, OrderUpdateRequestDto dto){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        Order updatedOrder = orderMapper.patchOrder(dto,order);
        orderRepository.save(updatedOrder);

        return orderMapper.toResponseDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(UUID id){
        if(!orderRepository.existsById(id)){
            throw new OrderNotFoundException(id);
        }

        orderRepository.deleteById(id);
    }
}
