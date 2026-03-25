package com.santiGalarza.order_management.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CategoryPatchRequestDto {

    private String name;
    private String description;
    private UUID parentCategoryId;
    private Boolean isActive;
}
