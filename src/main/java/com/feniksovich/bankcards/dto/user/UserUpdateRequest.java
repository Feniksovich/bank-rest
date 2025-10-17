package com.feniksovich.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 64)
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 64)
    private String firstName;
}
