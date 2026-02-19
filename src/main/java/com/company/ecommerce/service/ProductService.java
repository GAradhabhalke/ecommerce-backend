package com.company.ecommerce.service;

import com.company.ecommerce.dtos.CreateProductRequestDto;
import com.company.ecommerce.dtos.ProductResponseDto;
import com.company.ecommerce.entity.Category;
import com.company.ecommerce.entity.Product;
import com.company.ecommerce.repository.CategoryRepository;
import com.company.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponseDto createProduct(CreateProductRequestDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setQuantity(Optional.ofNullable(request.getQuantity()).orElse(0));
        product.setImageUrl(request.getImageUrl());

        return new ProductResponseDto(productRepository.save(product));
    }

    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResponseDto::new);
    }

    public Page<ProductResponseDto> searchProducts(String category, String searchTerm, Pageable pageable) {
        return productRepository.searchProducts(category, searchTerm, pageable).map(ProductResponseDto::new);
    }

    public ProductResponseDto updateProduct(Long id, CreateProductRequestDto request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setQuantity(Optional.ofNullable(request.getQuantity()).orElse(0));
        product.setImageUrl(request.getImageUrl());

        return new ProductResponseDto(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public ProductResponseDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponseDto::new)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
