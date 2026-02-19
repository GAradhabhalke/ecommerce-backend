package com.company.ecommerce.service;

import com.company.ecommerce.dtos.CreateUserRequestDto;
import com.company.ecommerce.dtos.UpdateUserDto;
import com.company.ecommerce.dtos.UserResponseDto;
import com.company.ecommerce.entity.Role;
import com.company.ecommerce.entity.User;
import com.company.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(CreateUserRequestDto request) {

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Use the role from the request, or default to CUSTOMER
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(Role.ROLE_CUSTOMER);
        }

        return mapToDto(userRepository.save(user));
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponseDto updateUserProfile(String email, UpdateUserDto updateUserDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fullName if provided
        if (updateUserDto.getFullName() != null && !updateUserDto.getFullName().isEmpty()) {
            user.setFullName(updateUserDto.getFullName());
        }

        // Update password if provided
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    private UserResponseDto mapToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
