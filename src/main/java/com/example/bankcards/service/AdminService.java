package com.example.bankcards.service;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CardService cardService;

    public ReadCardDto createNewCard(CreateCardDto createCardDto) {
        return cardService.createNewCard(createCardDto);
    }
}
