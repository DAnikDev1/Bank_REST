package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    @NotNull(message = "id of card sender can't be null")
    private Long fromCardId;

    @NotNull(message = "id of card receiver can't be null")
    private Long toCardId;

    @NotNull(message = "amount of money can't be null")
    @DecimalMin(value = "20.00", message = "amount must be more than 20.00")
    private BigDecimal amount;
}
