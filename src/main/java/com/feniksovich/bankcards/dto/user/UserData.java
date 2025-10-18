package com.feniksovich.bankcards.dto.user;

import com.feniksovich.bankcards.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserData {
    private UUID id;
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
}
