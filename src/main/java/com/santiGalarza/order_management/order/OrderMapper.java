package com.santiGalarza.order_management.order;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequestDto dto);
    OrderResponseDto toResponseDto(Order order);

    Order updateEntityFromRequestDto(OrderRequestDto dto, @MappingTarget Order order);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order patchOrder(OrderPatchRequestDto dto, @MappingTarget Order order);
}
