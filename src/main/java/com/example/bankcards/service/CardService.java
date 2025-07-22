package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.card.CreateBlockRequestDto;
import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.exception.UnprocessableEntityException;
import com.example.bankcards.exception.NoAccessException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardSequenceUtil;
import com.example.bankcards.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardSequenceUtil cardSequenceUtil;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final EncryptionUtil encryptionUtil;
    private final BlockRequestRepository blockRequestRepository;

    private static final String BIN = "000000";

    public Card findById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card was not found"));
    }

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
            throw new UnprocessableEntityException("User can't have more than 2 cards");
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

    public Page<ReadCardDto> getAllCards(Pageable pageable) {
        Page<Card> cardEntitiesPage = cardRepository.findAll(pageable);
        return toReadDtoCards(cardEntitiesPage);
    }

    public Page<ReadCardDto> getCardsByUserId(Long userId, Pageable pageable) {
        Page<Card> cardEntitiesPage = cardRepository.findAllByOwnerId(userId, pageable);

        return toReadDtoCards(cardEntitiesPage);
    }

    private Page<ReadCardDto> toReadDtoCards(Page<Card> cards) {
        return cards.map(card -> {
            ReadCardDto dto = cardMapper.toCardReadDto(card);

            String originalNumber = encryptionUtil.decrypt(card.getCardNumber());
            String maskedNumber = maskCardNumber(originalNumber);

            dto.setCardNumber(maskedNumber);
            return dto;
        });
    }

    @Transactional
    public void transferBetweenOwnCards(Long userId, @Valid TransferDto transferDto) {
        if (transferDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableEntityException("Amount to transfer must be greater than zero");
        }

        Card fromCard = findById(transferDto.getFromCardId());
        Card toCard = findById(transferDto.getToCardId());

        if (!Objects.equals(fromCard.getOwner().getId(), toCard.getOwner().getId()) ||
                !Objects.equals(fromCard.getOwner().getId(), userId)) {
            throw new NoAccessException("No access to transfer between not yours cards");
        }
        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new UnprocessableEntityException("Card of sender is not active");
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new UnprocessableEntityException("Card of receiver is not active");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(transferDto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferDto.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Transactional
    public BlockRequest createBlockRequest(Long userId, @NotNull Long cardId, @Valid CreateBlockRequestDto requestDto) {
        Card card = findById(cardId);

        if (!card.getOwner().getId().equals(userId)) {
            throw new NoAccessException("No access to block request this card");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new UnprocessableEntityException("Card of sender is not active");
        }

        if (blockRequestRepository.existsByCardAndStatus(card, BlockRequestStatus.IN_PROGRESS)) {
            throw new ConflictException("Request already exists");
        }

        User requester = userService.getReferenceById(userId);

        BlockRequest newRequest = new BlockRequest();
        newRequest.setCard(card);
        newRequest.setRequester(requester);
        newRequest.setReason(requestDto.getReason());
        newRequest.setStatus(BlockRequestStatus.IN_PROGRESS);

        return blockRequestRepository.save(newRequest);
    }

    public ReadCardDto getCardById(@NotNull Long cardId, Long userId) {
        Card card = findById(cardId);

        if (!card.getOwner().getId().equals(userId)) {
            throw new NoAccessException("No access to this card");
        }

        ReadCardDto dto = cardMapper.toCardReadDto(card);

        String originalNumber = encryptionUtil.decrypt(card.getCardNumber());
        String maskedNumber = maskCardNumber(originalNumber);

        dto.setCardNumber(maskedNumber);

        return dto;
    }
}
