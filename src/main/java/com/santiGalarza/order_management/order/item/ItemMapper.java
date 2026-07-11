package com.santiGalarza.order_management.order.item;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemResponse toResponseDto(Item item);
    Item toEntity(CreateItemRequest createItemRequest);
}
