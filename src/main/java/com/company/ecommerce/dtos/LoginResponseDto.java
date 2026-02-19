package com.company.ecommerce.dtos;

public class LoginResponseDto {

    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
