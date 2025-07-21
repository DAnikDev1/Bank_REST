package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReadUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Collection<String> roles;
    private boolean enabled;
    private boolean nonLocked;
    private int cardCount;
}
