package com.santiGalarza.order_management.category;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryPatchRequestDto {

    private String name;
    private String description;
    private UUID parentCategoryId;
    private Boolean isActive;
}
