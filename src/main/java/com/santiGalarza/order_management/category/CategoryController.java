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
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Valid  CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                categoryService.createCategory(categoryRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> fullyUpdateCategory(
            @PathVariable UUID id, @RequestBody @Valid CategoryRequestDto categoryRequestDto){
        return ResponseEntity.ok(categoryService.fullyUpdateCategory(id, categoryRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> partialUpdateCategory(
            @PathVariable UUID id, @RequestBody @Valid CategoryPatchRequestDto categoryPatchRequestDto){
        return ResponseEntity.ok(categoryService.partialUpdateCategory
                (id,categoryPatchRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
