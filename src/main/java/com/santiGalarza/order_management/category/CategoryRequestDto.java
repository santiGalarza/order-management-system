package com.santiGalarza.order_management.category;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CategoryRequestDto {

    @NotNull
    private String name;

    private String description;
    private UUID parentCategoryId;

    @NotNull
    private Boolean isActive;
}
