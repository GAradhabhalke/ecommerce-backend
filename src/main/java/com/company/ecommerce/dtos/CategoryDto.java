package com.company.ecommerce.dtos;

import java.util.Set;
import java.util.stream.Collectors;
import com.company.ecommerce.entity.Category;

public class CategoryDto {
    private Long id;
    private String name;
    private Set<CategoryDto> children;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            this.children = category.getChildren().stream()
                                    .map(CategoryDto::new)
                                    .collect(Collectors.toSet());
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<CategoryDto> getChildren() { return children; }
    public void setChildren(Set<CategoryDto> children) { this.children = children; }
}
