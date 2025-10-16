package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.security.JwtToken;

import java.util.UUID;

public interface UserRefreshTokenService {
    void track(JwtToken jwtToken);
    boolean isTracked(JwtToken jwtToken);
    void invalidate(JwtToken jwtToken);
    void invalidateAll(UUID userId);
}
