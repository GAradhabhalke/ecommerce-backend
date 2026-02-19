package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CreatePaymentIntentRequestDto;
import com.company.ecommerce.dtos.CreatePaymentIntentResponseDto;
import com.company.ecommerce.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final String webhookSecret;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService, @Value("${stripe.webhook.secret}") String webhookSecret) {
        this.paymentService = paymentService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentIntentResponseDto createPaymentIntent(@RequestBody CreatePaymentIntentRequestDto request) {
        String clientSecret = paymentService.createPaymentIntent(request.getOrderId());
        return new CreatePaymentIntentResponseDto(clientSecret);
    }

    @PostMapping("/stripe/webhook")
    public void handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Handle the event
            paymentService.handleStripeEvent(event);

        } catch (Exception e) {
            logger.error("Webhook error: ", e);
            // Invalid payload or signature
        }
    }
}
