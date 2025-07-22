package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.ReadUserDto;
import com.example.bankcards.dto.user.UserProfileDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.Collections;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "cardCount", expression = "java(user.getCards().size())")
    ReadUserDto toReadUserDto(User user);

    @Mapping(target = "cardCount", expression = "java(user.getCards().size())")
    UserProfileDto toUserProfileDto(User user);

    default Collection<String> mapRoles(Collection<Role> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(Role::getName)
                .toList();
    }
}
