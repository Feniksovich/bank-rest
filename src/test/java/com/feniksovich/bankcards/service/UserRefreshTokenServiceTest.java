package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.entity.UserRefreshToken;
import com.feniksovich.bankcards.repository.UserRefreshTokenRepository;
import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.service.auth.UserRefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRefreshTokenServiceTest {

    @Mock
    private UserRefreshTokenRepository repository;

    @InjectMocks
    private UserRefreshTokenServiceImpl userRefreshTokenService;

    private JwtToken refreshToken;
    private UserRefreshToken userRefreshToken;
    private UUID userId;
    private UUID tokenId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        tokenId = UUID.randomUUID();
        
        refreshToken = JwtToken.refreshToken(
                tokenId,
                userId,
                Instant.now(),
                Instant.now().plus(90, ChronoUnit.DAYS),
                Collections.emptyList()
        );

        userRefreshToken = UserRefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .expiresAt(refreshToken.expiresAt())
                .build();
    }

    @Test
    void track_WhenValidToken_ShouldSaveToken() {
        when(repository.save(any(UserRefreshToken.class))).thenReturn(userRefreshToken);

        userRefreshTokenService.track(refreshToken);

        verify(repository).save(any(UserRefreshToken.class));
    }

    @Test
    void isTracked_WhenTokenExists_ShouldReturnTrue() {
        when(repository.existsById(tokenId)).thenReturn(true);

        final boolean result = userRefreshTokenService.isTracked(refreshToken);

        assertThat(result).isTrue();
        verify(repository).existsById(tokenId);
    }

    @Test
    void isTracked_WhenTokenNotExists_ShouldReturnFalse() {
        when(repository.existsById(tokenId)).thenReturn(false);

        final boolean result = userRefreshTokenService.isTracked(refreshToken);

        assertThat(result).isFalse();
        verify(repository).existsById(tokenId);
    }

    @Test
    void invalidate_WhenValidToken_ShouldDeleteToken() {
        doNothing().when(repository).deleteById(tokenId);

        userRefreshTokenService.invalidate(refreshToken);

        verify(repository).deleteById(tokenId);
    }

    @Test
    void invalidateAll_WhenValidUserId_ShouldDeleteAllUserTokens() {
        doNothing().when(repository).deleteAllByUserId(userId);

        userRefreshTokenService.invalidateAll(userId);

        verify(repository).deleteAllByUserId(userId);
    }
}
