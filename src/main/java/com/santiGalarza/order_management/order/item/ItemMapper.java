package com.santiGalarza.order_management.order.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(expression = "java(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))", target = "totalPrice")
    ItemResponse toResponseDto(Item item);

    Item toEntity(CreateItemRequest createItemRequest);
}
