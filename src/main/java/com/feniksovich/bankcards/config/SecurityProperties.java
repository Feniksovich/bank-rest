package com.feniksovich.bankcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;

/**
 * Параметры безопасности приложения: JWK и сроки жизни токенов, ключ AES.
 */
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private final TokenProperties accessToken;
    private final TokenProperties refreshToken;
    private final CryptoProperties crypto;

    @ConstructorBinding
    public SecurityProperties(
            TokenProperties accessToken,
            TokenProperties refreshToken,
            CryptoProperties crypto
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.crypto = crypto;
    }

    public TokenProperties accessToken() {
        return accessToken;
    }

    public TokenProperties refreshToken() {
        return refreshToken;
    }

    public CryptoProperties crypto() {
        return crypto;
    }

    public record TokenProperties(
            String jwk,
            Duration expiration
    ) {}

    public record CryptoProperties(
            String aesKeyBase64
    ) {}

}
