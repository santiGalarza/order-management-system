package com.santiGalarza.order_management.category;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CategoryRequestDto {

    @NotNull
    private String name;

    private String description;
    private UUID parentCategoryId;

    @NotNull
    private Boolean isActive;
}
