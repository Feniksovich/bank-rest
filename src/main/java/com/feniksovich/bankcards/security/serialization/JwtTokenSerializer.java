package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;

/**
 * Контракт сериализации доменной модели JwtToken в строковый JWT.
 */
public interface JwtTokenSerializer {
    /**
     * @return Сериализаованный в строку JWT-токен
     */
    String serialize(JwtToken jwtToken);
}
