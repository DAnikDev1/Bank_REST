package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.card.CreateBlockRequestDto;
import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.user.UserProfileDto;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@Tag(name = "Card API", description = "API for managing cards")
@RequestMapping("/api/v1")
@Slf4j
public class CardController {
    private final CardService cardService;

    @Operation(summary = "Get own cards from jwt token")
    @GetMapping("/cards")
    @PreAuthorize("hasAuthority('READ_OWN_CARDS')")
    public ResponseEntity<Page<ReadCardDto>> readOwnCards(Authentication authentication, Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();
        Long userId = currentUser.getId();

        Page<ReadCardDto> cards = cardService.getCardsByUserId(userId, pageable);

        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "Transfer money between own cards")
    @PostMapping("/cards")
    @PreAuthorize("hasAuthority('TRANSFER_MONEY')")
    public ResponseEntity<Void> executeTransfer(@Valid @RequestBody TransferDto transferDto, Authentication authentication) {
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();
        cardService.transferBetweenOwnCards(currentUser.getId(), transferDto);

        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Create a block request for card")
    @PostMapping("/cards/{cardId}/block-request")
    @PreAuthorize("hasAuthority('CREATE_BLOCK_CARD_REQUEST')")
    public ResponseEntity<Void> createBlockRequest(@PathVariable @NotNull Long cardId, Authentication authentication, @Valid @RequestBody CreateBlockRequestDto requestDto) {
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();

        BlockRequest newBlockRequest = cardService.createBlockRequest(
                currentUser.getId(), cardId, requestDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{requestId}")
                .buildAndExpand(newBlockRequest.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
    @Operation(summary = "Get balance on own card")
    @GetMapping("/cards/{cardId}")
    @PreAuthorize("hasAuthority('READ_OWN_CARDS')")
    public ResponseEntity<ReadCardDto> readOwnCards(@PathVariable @NotNull Long cardId, Authentication authentication) {
        UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();
        Long userId = currentUser.getId();

        ReadCardDto card = cardService.getCardById(cardId, userId);

        return ResponseEntity.ok(card);
    }

}
