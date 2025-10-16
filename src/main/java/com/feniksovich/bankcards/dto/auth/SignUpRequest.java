package com.feniksovich.bankcards.dto.auth;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank
    @Size(min = 2, max = 64)
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 64)
    private String firstName;

    @NotBlank
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER)
    private String phoneNumber;

    @NotBlank
    @Size(min = 12, max = 64)
    private String password;
}
