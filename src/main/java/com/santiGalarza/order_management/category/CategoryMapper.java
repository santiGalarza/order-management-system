package com.santiGalarza.order_management.category;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CreateCategoryRequest createCategoryRequest);

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "parentCategory", ignore = true)
    void updateCategory(UpdateCategoryRequest updateCategoryRequest, @MappingTarget Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parentCategory", ignore = true)
    void patchCategory(PatchCategoryRequest patchCategoryRequest, @MappingTarget Category category);
}