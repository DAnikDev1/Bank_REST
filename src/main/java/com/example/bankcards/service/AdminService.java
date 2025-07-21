package com.example.bankcards.service;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.user.ReadUserDto;
import com.example.bankcards.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CardService cardService;
    private final UserService userService;

    public ReadCardDto createNewCard(CreateCardDto createCardDto) {
        return cardService.createNewCard(createCardDto);
    }

    public Page<ReadCardDto> getAllCards(Pageable pageable) {
        return cardService.getAllCards(pageable);
    }

    public Page<ReadUserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    public void lockUserById(Long userId) {
        userService.lockUserById(userId);
    }

    public void disableUserById(Long userId) {
        userService.disableUserById(userId);
    }

    public ReadUserDto findUserById(Long userId) {
        return userService.findReadUserDtoById(userId);
    }
}
