package com.feniksovich.bankcards.dto.user;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignInRequest {
    @NotBlank
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER)
    private String phoneNumber;

    @NotBlank
    private String password;
}
