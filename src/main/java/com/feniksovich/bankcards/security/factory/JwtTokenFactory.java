package com.feniksovich.bankcards.security.factory;

import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;

/**
 * Общий интерфейс фабрик токенов JWT.
 */
public interface JwtTokenFactory {
    /**
     * Генерирует токен для указанного пользователя.
     */
    JwtToken generate(UserPrincipal principal);
}
