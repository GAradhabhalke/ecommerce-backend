package com.company.ecommerce.service;

import com.company.ecommerce.entity.Order;
import com.company.ecommerce.entity.OrderStatus;
import com.company.ecommerce.repository.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String createPaymentIntent(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Stripe requires the amount in the smallest currency unit (e.g., cents)
        long amountInCents = (long) (order.getTotalAmount() * 100);

        try {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(amountInCents)
                            .setCurrency("usd") // You can make this configurable
                            .putMetadata("order_id", order.getId().toString())
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            // Handle Stripe API errors
            throw new RuntimeException("Error creating payment intent: " + e.getMessage());
        }
    }

    // NEW: Handle events from Stripe
    public void handleStripeEvent(Event event) {
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
            String orderId = paymentIntent.getMetadata().get("order_id");

            logger.info("✅ Payment succeeded for order_id: {}", orderId);

            // Update order status in your database
            Order order = orderRepository.findById(Long.parseLong(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found for webhook"));
            order.setStatus(OrderStatus.CONFIRMED); // Or PAID, or whatever your status is
            orderRepository.save(order);
        } else {
            logger.warn("Unhandled event type: {}", event.getType());
        }
    }
}
