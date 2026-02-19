package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.LoginRequestDto;
import com.company.ecommerce.dtos.LoginResponseDto;
import com.company.ecommerce.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto request) {
        String token = authService.login(request);
        return new LoginResponseDto(token);
    }
}
