package com.example.bankcards.controller;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.user.ReadUserDto;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@Tag(name = "Admin API", description = "API for managing everything")
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Register new card")
    @PostMapping("/cards")
    @PreAuthorize("hasAuthority('MANAGE_CARDS')")
    public ResponseEntity<ReadCardDto> createCard(@Valid @RequestBody CreateCardDto createCardDto) {
        ReadCardDto readCardDto = adminService.createNewCard(createCardDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(readCardDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(readCardDto);
    }

    @Operation(summary = "Get all cards")
    @GetMapping("/cards")
    @PreAuthorize("hasAuthority('MANAGE_CARDS')")
    public ResponseEntity<Page<ReadCardDto>> readAllCards(Pageable pageable) {
        Page<ReadCardDto> cardsPage = adminService.getAllCards(pageable);
        return ResponseEntity.ok(cardsPage);
    }

    @Operation(summary = "Get all users")
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Page<ReadUserDto>> readAllUsers(Pageable pageable) {
        Page<ReadUserDto> cardsPage = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(cardsPage);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<ReadUserDto> readUserById(@PathVariable Long userId) {
        ReadUserDto readUserDto = adminService.findUserById(userId);
        return ResponseEntity.ok(readUserDto);
    }

    @Operation(summary = "Lock user by id")
    @PatchMapping("/users/lock/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> lockUserById(@PathVariable Long userId) {
        adminService.lockUserById(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Disable user by id")
    @PatchMapping("/users/disable/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> disableUserById(@PathVariable Long userId) {
        adminService.disableUserById(userId);
        return ResponseEntity.ok().build();
    }
}
