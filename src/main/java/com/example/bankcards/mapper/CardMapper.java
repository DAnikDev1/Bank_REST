package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.ReadCardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "cardNumber", ignore = true)
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expiration", target = "expiration")
    ReadCardDto toCardReadDto(Card card);

    default String mapExpiration(YearMonth expiration) {
        if (expiration == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiration.format(formatter);
    }
    default String mapStatus(CardStatus status) {
        return status.name();
    }
}
