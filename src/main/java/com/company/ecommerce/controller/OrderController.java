package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CreateOrderRequestDto;
import com.company.ecommerce.dtos.UpdateOrderStatusRequestDto;
import com.company.ecommerce.entity.Order;
import com.company.ecommerce.entity.OrderStatus;
import com.company.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order placeOrder(Principal principal, @RequestBody CreateOrderRequestDto request) {
        return orderService.placeOrder(principal.getName(), request.getShippingAddress());
    }

    // ✅ NEW: Get all orders for the authenticated user
    @GetMapping
    public List<Order> getOrders(Principal principal) {
        return orderService.getOrdersForUser(principal.getName());
    }

    // CHECK ORDER STATUS (for customers)
    @GetMapping("/{orderId}/status")
    public OrderStatus getOrderStatus(@PathVariable Long orderId) {
        return orderService.getOrderStatus(orderId);
    }

    // ✅ NEW: Update order status (for admins)
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateOrderStatusRequestDto request) {
        return orderService.updateOrderStatus(orderId, request.getStatus());
    }
}
