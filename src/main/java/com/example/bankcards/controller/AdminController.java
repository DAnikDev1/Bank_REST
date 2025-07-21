package com.example.bankcards.controller;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
