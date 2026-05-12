package com.santiGalarza.order_management.order;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface OrderMapper {

    Order toEntity(OrderRequestDto dto);
    OrderResponseDto toResponseDto(Order order);


}
