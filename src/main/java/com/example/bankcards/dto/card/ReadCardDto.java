package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadCardDto {
    private Long id;
    private String cardNumber;
    private String expiration;
    private String status;
    private BigDecimal balance;
}
