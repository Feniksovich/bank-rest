package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.security.JwtToken;

import java.util.UUID;

/**
 * Сервис отслеживания и управления refresh-токенами пользователей.
 */
public interface UserRefreshTokenService {
    /**
     * Регистрирует refresh-токен как действующий.
     *
     * @param jwtToken refresh-токен
     */
    void track(JwtToken jwtToken);

    /**
     * Проверяет, отслеживается ли указанный refresh-токен.
     *
     * @param jwtToken refresh-токен
     * @return true, если токен найден; иначе false
     */
    boolean isTracked(JwtToken jwtToken);

    /**
     * Инвалидирует указанный refresh-токен.
     *
     * @param jwtToken refresh-токен
     */
    void invalidate(JwtToken jwtToken);

    /**
     * Инвалидирует все refresh-токены пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void invalidateAll(UUID userId);
}
