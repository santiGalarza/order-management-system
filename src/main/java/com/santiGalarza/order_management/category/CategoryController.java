package com.santiGalarza.order_management.category;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody @Valid CreateCategoryRequest createCategoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                categoryService.createCategory(createCategoryRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> fullyUpdateCategory(
            @PathVariable UUID id, @RequestBody @Valid UpdateCategoryRequest updateCategoryRequest){
        return ResponseEntity.ok(categoryService.fullyUpdateCategory(id, updateCategoryRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> partialUpdateCategory(
            @PathVariable UUID id, @RequestBody @Valid PatchCategoryRequest patchCategoryRequest){
        return ResponseEntity.ok(categoryService.partialUpdateCategory
                (id,patchCategoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
