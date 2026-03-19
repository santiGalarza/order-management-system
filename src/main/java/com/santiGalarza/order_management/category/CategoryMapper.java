package com.santiGalarza.order_management.category;

import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentCategory",source = "parentCategoryId",qualifiedByName = "uuidToCategory")
    Category toEntity(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto toCategoryResponseDto(Category category);

    @Mapping(target = "parentCategory",ignore = true)
    void updateCategory(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parentCategory", ignore = true)
    void patchCategory(CategoryPatchRequestDto categoryRequestDto, @MappingTarget Category category);
}
