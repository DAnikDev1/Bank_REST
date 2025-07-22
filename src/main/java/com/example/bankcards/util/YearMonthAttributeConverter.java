package com.example.bankcards.util;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;

import java.time.YearMonth;
import java.util.Optional;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        return Optional.ofNullable(attribute)
                .map(YearMonth::toString)
                .orElse(null);
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData)
                .map(YearMonth::parse)
                .orElse(null);
    }
}
