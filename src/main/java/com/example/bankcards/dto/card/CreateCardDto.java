package com.example.bankcards.dto.card;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardDto {
    @NotBlank(message = "User Id can't be blank")
    Long userId;
}
