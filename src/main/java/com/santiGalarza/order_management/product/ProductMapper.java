package com.santiGalarza.order_management.product;

import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {
    ProductResponseDto productToProductResponseDTO(Product product);
    Product productRequestDTOtoProduct(ProductRequestDto productRequestDTO);
}
