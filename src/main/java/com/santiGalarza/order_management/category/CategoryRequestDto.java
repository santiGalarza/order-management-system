package com.santiGalarza.order_management.category;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class CategoryRequestDto {

    @NotNull
    private String name;

    private String description;

    private UUID parentCategoryId;

    private boolean isActive;
}
