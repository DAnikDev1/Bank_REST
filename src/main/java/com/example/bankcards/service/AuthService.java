package com.example.bankcards.service;

import com.example.bankcards.dto.auth.login.LoginResponseDto;
import com.example.bankcards.dto.auth.login.LoginUserDto;
import com.example.bankcards.dto.auth.register.CreateUserDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ConflictException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtOperator;
import com.example.bankcards.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtOperator jwtOperator;

    @Transactional
    public void registerUser(CreateUserDto createUserDto) {
        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User user = User.builder()
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .firstName(createUserDto.getFirstName())
                .lastName(createUserDto.getLastName())
                .roles(Collections.singleton(userRole))
                .nonLocked(true)
                .enabled(true)
                .build();
        userRepository.save(user);
    }
    public LoginResponseDto loginUser(LoginUserDto loginDto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwtToken = jwtOperator.generateToken(userPrincipal);

        return new LoginResponseDto(jwtToken);
    }

}
