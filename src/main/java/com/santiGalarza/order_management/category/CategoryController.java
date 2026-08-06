package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.category.dto.CategoryResponse;
import com.santiGalarza.order_management.category.dto.CreateCategoryRequest;
import com.santiGalarza.order_management.category.dto.PatchCategoryRequest;
import com.santiGalarza.order_management.category.dto.UpdateCategoryRequest;
import jakarta.validation.Valid;

import com.santiGalarza.order_management.security.RequiresPermission;

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
    @RequiresPermission.CategoryRead
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @GetMapping
    @RequiresPermission.CategoryRead
    public ResponseEntity<List<CategoryResponse>> getCategories(){
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping
    @RequiresPermission.CategoryCreate
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody @Valid CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @RequiresPermission.CategoryUpdate
    public ResponseEntity<CategoryResponse> replaceCategory(
            @PathVariable UUID id, @RequestBody @Valid UpdateCategoryRequest request){
        return ResponseEntity.ok(categoryService.replaceCategory(id, request));
    }

    @PatchMapping("/{id}")
    @RequiresPermission.CategoryUpdate
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id, @RequestBody @Valid PatchCategoryRequest request){
        return ResponseEntity.ok(categoryService.updateCategory(id,request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission.CategoryDelete
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
