package com.company.ecommerce.dtos;

public class CreatePaymentIntentResponseDto {
    private String clientSecret;

    public CreatePaymentIntentResponseDto(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
