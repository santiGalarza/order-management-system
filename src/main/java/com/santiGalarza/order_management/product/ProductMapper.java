package com.santiGalarza.order_management.product;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto toProductResponseDto(Product product);
    Product productRequestDTOtoProduct(ProductRequestDto productRequestDTO);
    void updateProductRequestDto(ProductUpdateDto productRequestDTO, @MappingTarget Product product);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProductFromRequestDto(ProductPatchRequestDto productPatchRequestDto, @MappingTarget Product product);
}
