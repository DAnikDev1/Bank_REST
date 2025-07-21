package com.example.bankcards.service;

import com.example.bankcards.dto.user.ReadUserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User was not found"));
    }
    public ReadUserDto findReadUserDtoById(Long id) {
        User user = findById(id);
        return userMapper.toReadUserDto(user);
    }
    public Page<ReadUserDto> getAllUsers(Pageable pageable) {
        Page<User> cardEntitiesPage = userRepository.findAll(pageable);

        return cardEntitiesPage.map(userMapper::toReadUserDto);
    }

    @Transactional
    public void lockUserById(Long userId) {
        User user = findById(userId);
        user.setNonLocked(!user.getNonLocked());
        userRepository.save(user);
    }
    @Transactional
    public void disableUserById(Long userId) {
        User user = findById(userId);
        user.setEnabled(!user.getEnabled());
        userRepository.save(user);
    }
}
