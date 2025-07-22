
package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CardControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testReadOwnCardsSuccess() throws Exception {
        ReadCardDto mockCard = new ReadCardDto(1L, "**** **** **** 0005", "12/27", CardStatus.ACTIVE.toString(), BigDecimal.ZERO);
        List<ReadCardDto> cardList = new ArrayList<>();
        cardList.add(mockCard);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReadCardDto> mockPage = new PageImpl<>(cardList, pageable, 1);

        Authentication authentication = Mockito.mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(1L, "test@email.com", "", Collections.emptySet(), true, true);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        Mockito.when(cardService.getCardsByUserId(Mockito.eq(1L), Mockito.any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards")
                        .principal(authentication)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void testExecuteTransferSuccess() throws Exception {
        TransferDto dto = new TransferDto(1L, 2L, BigDecimal.valueOf(100));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(1L, "test@email.com", "", Collections.emptySet(), true, true);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        Mockito.doNothing().when(cardService).transferBetweenOwnCards(Mockito.eq(1L), Mockito.any(TransferDto.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cards")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testReadOwnCardByIdSuccess() throws Exception {
        Long cardId = 1L;
        ReadCardDto mockCard = new ReadCardDto(1L, "**** **** **** 0005", "12/27", CardStatus.ACTIVE.toString(), BigDecimal.ZERO);

        Authentication authentication = Mockito.mock(Authentication.class);
        UserPrincipal principal = new UserPrincipal(1L, "test@email.com", "", Collections.emptySet(), true, true);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        Mockito.when(cardService.getCardById(cardId, 1L)).thenReturn(mockCard);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cards/{cardId}", cardId)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }
} 