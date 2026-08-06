package com.santiGalarza.order_management.category;

import com.santiGalarza.order_management.category.dto.CreateCategoryRequest;
import com.santiGalarza.order_management.category.dto.PatchCategoryRequest;
import com.santiGalarza.order_management.category.dto.UpdateCategoryRequest;
import com.santiGalarza.order_management.category.exception.CategoryNotFoundException;

import com.santiGalarza.order_management.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    private Category resolveParent(UUID parentCategoryId) {
        if (parentCategoryId == null) return null;
        return categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException(parentCategoryId));
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setParentCategory(resolveParent(request.getParentCategoryId()));

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse replaceCategory(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryMapper.updateCategory(request, category);
        category.setParentCategory(resolveParent(request.getParentCategoryId()));

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, PatchCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryMapper.patchCategory(request, category);
        if (request.getParentCategoryId() != null) {
            category.setParentCategory(resolveParent(request.getParentCategoryId()));
        }

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id){
        if(!categoryRepository.existsById(id)){
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }
}
