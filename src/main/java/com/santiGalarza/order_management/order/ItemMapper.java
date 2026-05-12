package com.santiGalarza.order_management.order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemResponseDto toResponseDto(Item item);
    Item toEntity(ItemRequestDto dto);
}
