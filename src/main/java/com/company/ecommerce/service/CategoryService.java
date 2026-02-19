package com.company.ecommerce.service;

import com.company.ecommerce.dtos.CategoryDto;
import com.company.ecommerce.dtos.CreateCategoryRequestDto;
import com.company.ecommerce.entity.Category;
import com.company.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(CreateCategoryRequestDto request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return rootCategories.stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    public void deleteCategory(Long id) {
        // Note: This is a hard delete. In a real app, you might want to check
        // if the category has products or sub-categories before deleting.
        categoryRepository.deleteById(id);
    }
}
