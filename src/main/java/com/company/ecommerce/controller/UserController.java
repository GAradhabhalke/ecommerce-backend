package com.company.ecommerce.controller;

import com.company.ecommerce.dtos.CreateUserRequestDto;
import com.company.ecommerce.dtos.UpdateUserDto;
import com.company.ecommerce.dtos.UserResponseDto;
import com.company.ecommerce.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE USER
    @PostMapping
    public UserResponseDto createUser(@RequestBody CreateUserRequestDto request) {
        return userService.createUser(request);
    }

    // NEW: UPDATE CURRENT USER'S PROFILE
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(Principal principal, @RequestBody UpdateUserDto updateUserDto) {
        UserResponseDto updatedUser = userService.updateUserProfile(principal.getName(), updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    // GET ALL USERS
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
