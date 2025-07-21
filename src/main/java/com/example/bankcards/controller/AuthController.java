package com.example.bankcards.controller;

import com.example.bankcards.dto.auth.login.LoginUserDto;
import com.example.bankcards.dto.auth.register.CreateUserDto;
import com.example.bankcards.dto.auth.login.LoginResponseDto;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "Auth API", description = "API for authorization")
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        authService.registerUser(createUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Login existed user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginUserDto loginDto) {
        LoginResponseDto response = authService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }
}
