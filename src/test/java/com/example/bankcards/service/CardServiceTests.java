package com.example.bankcards.service;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
        Mockito.when(cardService.generateCardNumber()).thenReturn("0000001000000005");
        Mockito.when(encryptionUtil.encrypt(Mockito.anyString())).thenReturn("encrypted_number");
        Mockito.when(encryptionUtil.hash(Mockito.anyString())).thenReturn("hashed_number");
        when(cardMapper.toCardReadDto(any(Card.class))).thenReturn(new ReadCardDto());

        cardService.createNewCard(dto);
        Mockito.verify(cardRepository, Mockito.times(1)).save(any(Card.class));
        ArgumentCaptor<Card> cardArgumentCaptor = ArgumentCaptor.forClass(Card.class);
        Card savedCard = cardArgumentCaptor.getValue();

        Assertions.assertEquals("encrypted_number", savedCard.getCardNumber());
        Assertions.assertEquals("hashed_number", savedCard.getHashedCardNumber());
        Assertions.assertEquals(testUser, savedCard.getOwner());
        Assertions.assertEquals(CardStatus.ACTIVE, savedCard.getStatus());
        Assertions.assertEquals(0, BigDecimal.ZERO.compareTo(savedCard.getBalance()));
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
