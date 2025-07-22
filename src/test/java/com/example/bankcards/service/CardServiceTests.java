package com.example.bankcards.service;

import com.example.bankcards.dto.card.CreateBlockRequestDto;
import com.example.bankcards.dto.card.CreateCardDto;
import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardSequenceUtil;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.enums.BlockRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.example.bankcards.exception.UnprocessableEntityException;

@ExtendWith(MockitoExtension.class)
public class CardServiceTests {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardSequenceUtil cardSequenceUtil;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private UserService userService;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private BlockRequestRepository blockRequestRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    public void testCreateNewCard() {
        CreateCardDto dto = new CreateCardDto(1L);
        User testUser = createBasicUser();

        Mockito.when(userService.findById(1L)).thenReturn(testUser);
        Mockito.when(cardRepository.countByOwner(testUser)).thenReturn(0L);
        Mockito.when(cardSequenceUtil.getNextSeriesNumber()).thenReturn(100000000L);
        Mockito.when(cardSequenceUtil.calculateLuhnCheckDigit(Mockito.anyString())).thenReturn(5);
        
        Mockito.when(encryptionUtil.encrypt(Mockito.anyString())).thenReturn("encrypted_number");
        Mockito.when(encryptionUtil.hash(Mockito.anyString())).thenReturn("hashed_number");
        Mockito.when(cardMapper.toCardReadDto(Mockito.any(Card.class))).thenReturn(new ReadCardDto());

        ReadCardDto result = cardService.createNewCard(dto);

        Mockito.verify(cardRepository, Mockito.times(1)).save(Mockito.any(Card.class));
        
        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        Mockito.verify(cardRepository).save(cardCaptor.capture());
        Card savedCard = cardCaptor.getValue();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("encrypted_number", savedCard.getCardNumber());
        Assertions.assertEquals("hashed_number", savedCard.getHashedCardNumber());
        Assertions.assertEquals(testUser, savedCard.getOwner());
        Assertions.assertEquals(CardStatus.ACTIVE, savedCard.getStatus());
        Assertions.assertEquals(BigDecimal.ZERO, savedCard.getBalance());
        Assertions.assertEquals(YearMonth.now().plusYears(3), savedCard.getExpiration());
    }

    @Test
    public void testCreateNewCardUserHasMaxCardsThrowsException() {
        CreateCardDto dto = new CreateCardDto(1L);
        User testUser = createBasicUser();

        Mockito.when(userService.findById(1L)).thenReturn(testUser);
        Mockito.when(cardRepository.countByOwner(testUser)).thenReturn(2L);

        Assertions.assertThrows(UnprocessableEntityException.class, () -> cardService.createNewCard(dto));
        
        Mockito.verify(cardRepository, Mockito.never()).save(Mockito.any(Card.class));
    }

    @Test
    public void testTransferBetweenOwnCardsSuccess() {
        Long userId = 1L;
        TransferDto dto = new TransferDto(1L, 2L, BigDecimal.valueOf(100));
        
        Card fromCard = Card.builder().id(1L).balance(BigDecimal.valueOf(200)).status(CardStatus.ACTIVE).owner(createBasicUser()).build();
        Card toCard = Card.builder().id(2L).balance(BigDecimal.ZERO).status(CardStatus.ACTIVE).owner(createBasicUser()).build();
        
        Mockito.when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        Mockito.when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        
        cardService.transferBetweenOwnCards(userId, dto);
        
        // Проверяем балансы
        Assertions.assertEquals(BigDecimal.valueOf(100), fromCard.getBalance());
        Assertions.assertEquals(BigDecimal.valueOf(100), toCard.getBalance());
        
        Mockito.verify(cardRepository, Mockito.times(1)).save(fromCard);
        Mockito.verify(cardRepository, Mockito.times(1)).save(toCard);
    }

    @Test
    public void testTransferBetweenOwnCardsInvalidAmountThrowsException() {
        TransferDto dto = new TransferDto(1L, 2L, BigDecimal.ZERO);
        
        Assertions.assertThrows(UnprocessableEntityException.class, () -> cardService.transferBetweenOwnCards(1L, dto));
        Mockito.verify(cardRepository, Mockito.never()).save(Mockito.any(Card.class));
    }

    @Test
    public void testCreateBlockRequestSuccess() {
        Long userId = 1L;
        Long cardId = 1L;
        CreateBlockRequestDto dto = new CreateBlockRequestDto("Test reason");
        
        Card card = Card.builder().id(cardId).status(CardStatus.ACTIVE).owner(createBasicUser()).build();
        User requester = createBasicUser();
        
        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(userService.getReferenceById(userId)).thenReturn(requester);
        Mockito.when(blockRequestRepository.existsByCardAndStatus(card, BlockRequestStatus.IN_PROGRESS)).thenReturn(false);
        Mockito.when(blockRequestRepository.save(Mockito.any(BlockRequest.class))).thenReturn(new BlockRequest());
        
        BlockRequest result = cardService.createBlockRequest(userId, cardId, dto);
        
        Assertions.assertNotNull(result);
        Mockito.verify(blockRequestRepository, Mockito.times(1)).save(Mockito.any(BlockRequest.class));
    }

    @Test
    public void testGetAllCardsSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Card mockCard = Card.builder().id(1L).cardNumber("encrypted_dummy").build();
        Page<Card> mockPage = new PageImpl<>(List.of(mockCard));
        
        Mockito.when(cardRepository.findAll(pageable)).thenReturn(mockPage);
        Mockito.when(encryptionUtil.decrypt(Mockito.any())).thenReturn("0000001000000005");
        Mockito.when(cardMapper.toCardReadDto(Mockito.any(Card.class))).thenReturn(new ReadCardDto());
        
        Page<ReadCardDto> result = cardService.getAllCards(pageable);
        
        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(cardMapper, Mockito.times(1)).toCardReadDto(Mockito.any(Card.class));
    }

    private User createBasicUser() {
        User testUser = User.builder()
                .id(1L)
                .firstName("TestFirstName")
                .lastName("TestLastName")
                .email("TestEmail@email.com")
                .build();
        return testUser;
    }
}
