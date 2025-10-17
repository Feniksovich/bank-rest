package com.feniksovich.bankcards.dto.auth;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignInRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
