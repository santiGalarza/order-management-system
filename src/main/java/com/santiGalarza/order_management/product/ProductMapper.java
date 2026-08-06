package com.santiGalarza.order_management.product;

import com.santiGalarza.order_management.product.dto.CreateProductRequest;
import com.santiGalarza.order_management.product.dto.PatchProductRequest;
import com.santiGalarza.order_management.product.dto.ProductResponse;
import com.santiGalarza.order_management.product.dto.UpdateProductRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "active", target = "isActive")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "isActive", target = "active")
    Product createProductRequestToProduct(CreateProductRequest createProductRequest);

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "isActive", target = "active")
    void updateProductFromRequest(UpdateProductRequest updateProductRequest, @MappingTarget Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "isActive", target = "active")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchProductFromRequest(PatchProductRequest patchProductRequest, @MappingTarget Product product);
}
