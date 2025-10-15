package com.feniksovich.bankcards.security;

import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

public record JwtToken(
        TokenType type,
        UUID id,
        UUID userId,
        Instant createdAt,
        Instant expiresAt,
        Collection<? extends GrantedAuthority> authorities
) {

    public static JwtToken accessToken(
            UUID id,
            UUID userId,
            Instant createdAt,
            Instant expiresAt,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new JwtToken(TokenType.ACCESS, id, userId, createdAt, expiresAt, authorities);
    }

    public static JwtToken refreshToken(
            UUID id,
            UUID userId,
            Instant createdAt,
            Instant expiresAt,
            Collection<? extends GrantedAuthority> authorities
    ) {
        return new JwtToken(TokenType.REFRESH, id, userId, createdAt, expiresAt, authorities);
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public Duration getRemainingTime() {
        return Duration.between(Instant.now(), expiresAt);
    }

    @Override
    public String toString() {
        return "JwtToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", authorities=" + authorities +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
