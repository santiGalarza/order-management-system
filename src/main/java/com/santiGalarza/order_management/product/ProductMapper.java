package com.santiGalarza.order_management.product;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {
    ProductResponseDto toProductResponseDto(Product product);
    Product productRequestDTOtoProduct(ProductRequestDto productRequestDTO);
    void updateProductRequestDto(ProductRequestDto productRequestDTO,@MappingTarget Product product);
}
