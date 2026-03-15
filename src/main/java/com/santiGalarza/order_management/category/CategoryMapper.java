package com.santiGalarza.order_management.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentCategory",source = "parentCategoryId",qualifiedByName = "uuidToCategory")
    Category toEntity(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto toCategoryResponseDto(Category category);

    @Named("uuidToCategory")
    default Category uuidToCategory(UUID id) {
        if(id == null) return null;
        Category parent = new Category();
        parent.setId(id);
        return parent;
    }

}
