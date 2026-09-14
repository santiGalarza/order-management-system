package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.category.dto.CategoryResponse;
import com.santiGalarza.order_management.category.dto.CreateCategoryRequest;
import com.santiGalarza.order_management.category.dto.PatchCategoryRequest;
import com.santiGalarza.order_management.category.dto.UpdateCategoryRequest;
import com.santiGalarza.order_management.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;

    @BeforeEach
    void setUp() {
        parentCategory = Category.create("Parent", "Parent description", true, null);
    }

    private CategoryResponse someCategoryResponse() {
        return new CategoryResponse(UUID.randomUUID(), "Test Category", "Test description", null, true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("getCategories")
    class GetCategories {

        @Test
        @DisplayName("returns all categories mapped to response dtos")
        void returnsAllMappedCategories() {
            Category category = Category.create("Category", "Description", true, null);
            CategoryResponse response = someCategoryResponse();

            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            List<CategoryResponse> result = categoryService.getCategories();

            assertThat(result).containsExactly(response);
        }
    }

    @Nested
    @DisplayName("getCategory")
    class GetCategory {

        @Test
        @DisplayName("returns the mapped category when found")
        void returnsMappedCategoryWhenFound() {
            Category category = Category.create("Category", "Description", true, null);
            CategoryResponse response = someCategoryResponse();
            UUID id = UUID.randomUUID();

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.getCategory(id);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when category is not found")
        void throwsWhenCategoryNotFound() {
            UUID id = UUID.randomUUID();

            when(categoryRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getCategory(id))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("createCategory")
    class CreateCategory {

        @Test
        @DisplayName("creates the category without a parent and returns the mapped result")
        void createsCategoryWithoutParent() {
            CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName("Category");
            request.setDescription("Description");
            request.setIsActive(true);

            Category categoryEntity = Category.create("Category", "Description", true, null);
            Category savedCategory = Category.create("Category", "Description", true, null);
            CategoryResponse response = someCategoryResponse();

            when(categoryMapper.toEntity(request)).thenReturn(categoryEntity);
            when(categoryRepository.save(categoryEntity)).thenReturn(savedCategory);
            when(categoryMapper.toCategoryResponse(savedCategory)).thenReturn(response);

            CategoryResponse result = categoryService.createCategory(request);

            assertThat(categoryEntity.getParentCategory()).isNull();
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("creates the category with a parent and returns the mapped result")
        void createsCategoryWithParent() {
            UUID parentId = UUID.randomUUID();

            CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName("Category");
            request.setDescription("Description");
            request.setIsActive(true);
            request.setParentCategoryId(parentId);

            Category categoryEntity = Category.create("Category", "Description", true, null);
            Category savedCategory = Category.create("Category", "Description", true, parentCategory);
            CategoryResponse response = someCategoryResponse();

            when(categoryMapper.toEntity(request)).thenReturn(categoryEntity);
            when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
            when(categoryRepository.save(categoryEntity)).thenReturn(savedCategory);
            when(categoryMapper.toCategoryResponse(savedCategory)).thenReturn(response);

            CategoryResponse result = categoryService.createCategory(request);

            assertThat(categoryEntity.getParentCategory()).isEqualTo(parentCategory);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when the parent category is not found")
        void throwsWhenParentCategoryNotFound() {
            UUID parentId = UUID.randomUUID();

            CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName("Category");
            request.setIsActive(true);
            request.setParentCategoryId(parentId);

            Category categoryEntity = Category.create("Category", "Description", true, null);

            when(categoryMapper.toEntity(request)).thenReturn(categoryEntity);
            when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("replaceCategory")
    class ReplaceCategory {

        @Test
        @DisplayName("replaces the category without a parent and returns the mapped result")
        void replacesCategoryWithoutParent() {
            Category category = Category.create("Old", "Old description", true, parentCategory);
            UUID id = UUID.randomUUID();

            UpdateCategoryRequest request = new UpdateCategoryRequest();
            request.setName("New");
            request.setDescription("New description");
            request.setIsActive(false);

            CategoryResponse response = someCategoryResponse();

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.replaceCategory(id, request);

            verify(categoryMapper).updateCategory(request, category);
            assertThat(category.getParentCategory()).isNull();
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("replaces the category with a parent and returns the mapped result")
        void replacesCategoryWithParent() {
            Category category = Category.create("Old", "Old description", true, null);
            UUID id = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            UpdateCategoryRequest request = new UpdateCategoryRequest();
            request.setName("New");
            request.setDescription("New description");
            request.setIsActive(true);
            request.setParentCategoryId(parentId);

            CategoryResponse response = someCategoryResponse();

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.replaceCategory(id, request);

            assertThat(category.getParentCategory()).isEqualTo(parentCategory);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when category is not found")
        void throwsWhenCategoryNotFound() {
            UUID id = UUID.randomUUID();
            UpdateCategoryRequest request = new UpdateCategoryRequest();
            request.setName("New");
            request.setIsActive(true);

            when(categoryRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.replaceCategory(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);
        }

        @Test
        @DisplayName("throws when the parent category is not found")
        void throwsWhenParentCategoryNotFound() {
            Category category = Category.create("Old", "Old description", true, null);
            UUID id = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            UpdateCategoryRequest request = new UpdateCategoryRequest();
            request.setName("New");
            request.setIsActive(true);
            request.setParentCategoryId(parentId);

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.replaceCategory(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategory {

        @Test
        @DisplayName("patches the category without touching the parent when parentCategoryId is null")
        void patchesCategoryWithoutParentChange() {
            Category category = Category.create("Old", "Old description", true, parentCategory);
            UUID id = UUID.randomUUID();

            PatchCategoryRequest request = new PatchCategoryRequest();
            request.setName("New");

            CategoryResponse response = someCategoryResponse();

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.updateCategory(id, request);

            verify(categoryMapper).patchCategory(request, category);
            assertThat(category.getParentCategory()).isEqualTo(parentCategory);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("patches the category and updates the parent when parentCategoryId is provided")
        void patchesCategoryWithParentChange() {
            Category category = Category.create("Old", "Old description", true, null);
            UUID id = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            PatchCategoryRequest request = new PatchCategoryRequest();
            request.setParentCategoryId(parentId);

            CategoryResponse response = someCategoryResponse();

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
            when(categoryRepository.save(category)).thenReturn(category);
            when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

            CategoryResponse result = categoryService.updateCategory(id, request);

            assertThat(category.getParentCategory()).isEqualTo(parentCategory);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when category is not found")
        void throwsWhenCategoryNotFound() {
            UUID id = UUID.randomUUID();
            PatchCategoryRequest request = new PatchCategoryRequest();

            when(categoryRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);
        }

        @Test
        @DisplayName("throws when the parent category is not found")
        void throwsWhenParentCategoryNotFound() {
            Category category = Category.create("Old", "Old description", true, null);
            UUID id = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            PatchCategoryRequest request = new PatchCategoryRequest();
            request.setParentCategoryId(parentId);

            when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
            when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.updateCategory(id, request))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCategory")
    class DeleteCategory {

        @Test
        @DisplayName("deletes the category when it exists")
        void deletesCategoryWhenExists() {
            UUID id = UUID.randomUUID();

            when(categoryRepository.existsById(id)).thenReturn(true);

            categoryService.deleteCategory(id);

            verify(categoryRepository).deleteById(id);
        }

        @Test
        @DisplayName("throws when category is not found")
        void throwsWhenCategoryNotFound() {
            UUID id = UUID.randomUUID();

            when(categoryRepository.existsById(id)).thenReturn(false);

            assertThatThrownBy(() -> categoryService.deleteCategory(id))
                    .isInstanceOf(CategoryNotFoundException.class);

            verify(categoryRepository, never()).deleteById(any());
        }
    }
}