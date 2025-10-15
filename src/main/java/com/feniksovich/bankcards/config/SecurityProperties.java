package com.feniksovich.bankcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private final TokenProperties accessTokenProperties;
    private final TokenProperties refreshTokenProperties;

    public SecurityProperties(TokenProperties accessToken, TokenProperties refreshToken) {
        this.accessTokenProperties = accessToken;
        this.refreshTokenProperties = refreshToken;
    }

    public TokenProperties accessToken() {
        return accessTokenProperties;
    }

    public TokenProperties refreshToken() {
        return refreshTokenProperties;
    }

    public record TokenProperties(
            String jwk,
            Duration expiration
    ) {}

}
