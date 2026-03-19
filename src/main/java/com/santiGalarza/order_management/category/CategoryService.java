package com.santiGalarza.order_management.category;

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

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toCategoryResponseDto(category);
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        category.setParentCategory(resolveParent(categoryRequestDto.getParentCategoryId()));

        return categoryMapper.toCategoryResponseDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDto fullyUpdateCategory(UUID id, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryMapper.updateCategory(categoryRequestDto, category);
        category.setParentCategory(resolveParent(categoryRequestDto.getParentCategoryId()));

        return categoryMapper.toCategoryResponseDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponseDto partialUpdateCategory(UUID id, CategoryPatchRequestDto categoryPatchRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryMapper.patchCategory(categoryPatchRequestDto, category);
        if (categoryPatchRequestDto.getParentCategoryId() != null) {
            category.setParentCategory(resolveParent(categoryPatchRequestDto.getParentCategoryId()));
        }

        return categoryMapper.toCategoryResponseDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id){
        if(!categoryRepository.existsById(id)){
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }
}
