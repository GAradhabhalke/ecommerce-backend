package com.company.ecommerce.repository;

import com.company.ecommerce.entity.Cart;
import com.company.ecommerce.entity.CartItem;
import com.company.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // ✅ NEW: Find an item by its cart and product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
