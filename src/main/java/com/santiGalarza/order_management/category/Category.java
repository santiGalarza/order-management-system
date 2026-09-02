package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.common.base.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends Auditable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @NotNull
    private String name;

    private String description;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    public boolean isRootCategory() {
        return parentCategory == null;
    }

    public static Category create(String name, String description, boolean isActive, Category parentCategory) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(isActive);
        category.setParentCategory(parentCategory);
        return category;
    }
}
