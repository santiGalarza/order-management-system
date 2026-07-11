package com.santiGalarza.order_management.product;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toProductResponse(Product product);
    Product createProductRequestToProduct(CreateProductRequest createProductRequest);
    void updateProductFromRequest(UpdateProductRequest updateProductRequest, @MappingTarget Product product);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProductFromRequest(PatchProductRequest patchProductRequest, @MappingTarget Product product);
}
