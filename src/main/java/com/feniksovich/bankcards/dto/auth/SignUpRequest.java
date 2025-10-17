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
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 64)
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 64)
    private String firstName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexPatterns.PHONE_NUMBER, message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 64)
    private String password;
}
