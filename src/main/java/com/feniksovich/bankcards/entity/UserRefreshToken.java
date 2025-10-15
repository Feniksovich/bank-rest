package com.feniksovich.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "refresh_tokens")
public class UserRefreshToken {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof UserRefreshToken userRefreshToken)) {
            return false;
        }
        return id.equals(userRefreshToken.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
