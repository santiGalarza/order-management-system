package com.santiGalarza.order_management.order;

import com.santiGalarza.order_management.order.dto.CreateOrderRequest;
import com.santiGalarza.order_management.order.dto.OrderResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "currentStatus", ignore = true)
    Order toEntity(CreateOrderRequest createOrderRequest);

    @Mapping(source = "createdAt", target = "creationDate")
    @Mapping(source = "currentStatus.code", target = "statusCode")
    @Mapping(source = "currentStatus.label", target = "statusLabel")
    OrderResponse toResponseDto(Order order);
}