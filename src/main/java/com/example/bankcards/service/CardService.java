package com.example.bankcards.service;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardLimitExceededException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardSequenceUtil;
import com.example.bankcards.util.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardSequenceUtil cardSequenceUtil;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final EncryptionUtil encryptionUtil;

    private static final String BIN = "000000";

    public String generateCardNumber() {
        Long uniquePart = cardSequenceUtil.getNextSeriesNumber();
        String baseNumber = BIN + String.format("%09d", uniquePart);
        int checkDigit = cardSequenceUtil.calculateLuhnCheckDigit(baseNumber);

        return baseNumber + checkDigit;
    }

    @Transactional
    public ReadCardDto createNewCard(CreateCardDto createCardDto) {
        User user = userService.findById(createCardDto.getUserId());
        long existingCardsCount = cardRepository.countByOwner(user);
        if (existingCardsCount >= 2) {
            throw new CardLimitExceededException("User can't have more than 2 cards");
        }
        String cardNumber = generateCardNumber();

        Card card = Card.builder()
                .cardNumber(encryptionUtil.encrypt(cardNumber))
                .hashedCardNumber(encryptionUtil.hash(cardNumber))
                .owner(user)
                .expiration(YearMonth.now().plusYears(3))
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();
        cardRepository.save(card);

        ReadCardDto cardReadDto = cardMapper.toCardReadDto(card);
        cardReadDto.setCardNumber(maskCardNumber(cardNumber));
        return cardReadDto;
    }
    private String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

}
