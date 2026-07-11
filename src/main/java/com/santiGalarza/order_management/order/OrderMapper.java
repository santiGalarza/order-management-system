package com.santiGalarza.order_management.order;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface OrderMapper {

    Order toEntity(CreateOrderRequest createOrderRequest);
    OrderResponse toResponseDto(Order order);


}
