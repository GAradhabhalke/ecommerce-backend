package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.AddToCartRequestDto;
import com.company.ecommerce.entity.Cart;
import com.company.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ADD ITEM TO CART
    @PostMapping
    public Cart addToCart(Principal principal, @RequestBody AddToCartRequestDto request) {
        return cartService.addToCart(principal.getName(), request);
    }

    // GET CART ITEMS BY USER
    @GetMapping
    public Cart getCartByUser(Principal principal) {
        return cartService.getCartByUserId(principal.getName());
    }

    // REMOVE CART ITEM
    @DeleteMapping("/{id}")
    public void removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
    }
}
