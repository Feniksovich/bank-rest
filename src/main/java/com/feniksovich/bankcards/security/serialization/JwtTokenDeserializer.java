package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;

/**
 * Контракт десериализации строковых JWT в доменную модель.
 */
public interface JwtTokenDeserializer {
    /**
     * @return JwtToken или null, если токен невалиден
     */
    JwtToken deserialize(String token);
}
