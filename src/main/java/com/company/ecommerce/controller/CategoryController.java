package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CategoryDto;
import com.company.ecommerce.dtos.CreateCategoryRequestDto;
import com.company.ecommerce.entity.Category;
import com.company.ecommerce.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category createCategory(@RequestBody CreateCategoryRequestDto request) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryDto> getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
