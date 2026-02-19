package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CreateProductRequestDto;
import com.company.ecommerce.dtos.ProductResponseDto;
import com.company.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponseDto createProduct(@RequestBody CreateProductRequestDto request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public Page<ProductResponseDto> searchProducts(@RequestParam(required = false) String category,
                                                   @RequestParam(required = false) String searchTerm,
                                                   Pageable pageable) {
        return productService.searchProducts(category, searchTerm, pageable);
    }

    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody CreateProductRequestDto request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
