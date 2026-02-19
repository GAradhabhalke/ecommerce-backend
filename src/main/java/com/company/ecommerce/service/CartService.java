package com.company.ecommerce.service;

import com.company.ecommerce.dtos.AddToCartRequestDto;
import com.company.ecommerce.entity.Cart;
import com.company.ecommerce.entity.CartItem;
import com.company.ecommerce.entity.Product;
import com.company.ecommerce.entity.User;
import com.company.ecommerce.repository.CartItemRepository;
import com.company.ecommerce.repository.CartRepository;
import com.company.ecommerce.repository.ProductRepository;
import com.company.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public Cart addToCart(String userEmail, AddToCartRequestDto request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // ✅ FIX: Check for existing item and update quantity
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cart.getItems().add(cartItem);
        }

        return cartRepository.save(cart);
    }

    public Cart getCartByUserId(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));
    }

    public void removeCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}
