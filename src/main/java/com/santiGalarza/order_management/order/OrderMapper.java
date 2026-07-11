package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.item.ItemMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface OrderMapper {
    Order toEntity(CreateOrderRequest createOrderRequest);

    @Mapping(source = "currentStatus.code", target = "statusCode")
    @Mapping(source = "currentStatus.label", target = "statusLabel")
    OrderResponse toResponseDto(Order order);
}
