package com.example.bankcards.controller;

import com.example.bankcards.dto.user.UserProfileDto;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "User API", description = "API for managing users")
@RequestMapping("/api/v1")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get yourself profile from jwt token")
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('READ_OWN_PROFILE')")
    public ResponseEntity<UserProfileDto> readOwnProfile(Authentication authentication) {
        log.info("Trying to get profile");
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();

        UserProfileDto userProfile = userService.getUserProfileById(currentUser.getId());
        log.info("User profile: {}", userProfile);
        return ResponseEntity.ok(userProfile);
    }
}
