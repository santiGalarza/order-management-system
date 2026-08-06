package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.category.dto.CategoryResponse;
import com.santiGalarza.order_management.category.dto.CreateCategoryRequest;
import com.santiGalarza.order_management.category.dto.PatchCategoryRequest;
import com.santiGalarza.order_management.category.dto.UpdateCategoryRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(source = "isActive", target = "active")
    Category toEntity(CreateCategoryRequest createCategoryRequest);

    @Mapping(source = "active", target = "isActive")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(source = "isActive", target = "active")
    void updateCategory(UpdateCategoryRequest updateCategoryRequest, @MappingTarget Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(source = "isActive", target = "active")
    void patchCategory(PatchCategoryRequest patchCategoryRequest, @MappingTarget Category category);
}