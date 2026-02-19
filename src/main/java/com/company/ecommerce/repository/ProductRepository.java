package com.company.ecommerce.repository;

import com.company.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
    @Query(value = "SELECT p.*, " +
            "(SELECT COALESCE(AVG(r.rating), 0) FROM reviews r WHERE r.product_id = p.id) as averageRating, " +
            "(SELECT COUNT(r.id) FROM reviews r WHERE r.product_id = p.id) as reviewCount " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE (:category IS NULL OR c.name = :category) AND " +
            "(:searchTerm IS NULL OR " +
            "LOWER(CAST(p.name AS TEXT)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CAST(p.description AS TEXT)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))",
            countQuery = "SELECT count(*) FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE (:category IS NULL OR c.name = :category) AND " +
                    "(:searchTerm IS NULL OR " +
                    "LOWER(CAST(p.name AS TEXT)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                    "LOWER(CAST(p.description AS TEXT)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("category") String category, @Param("searchTerm") String searchTerm, Pageable pageable);
}
