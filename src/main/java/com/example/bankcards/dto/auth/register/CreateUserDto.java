package com.example.bankcards.dto.auth.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @NotBlank(message = "first name can't be empty")
    private String firstName;

    @NotBlank(message = "last name can't be empty")
    private String lastName;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Wrong email format")
    private String email;

    @NotBlank(message = "Password can't be empty")
    @Size(min = 6, max = 64, message = "Password must be between 6-64 symbols")
    private String password;
}
