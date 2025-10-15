package com.feniksovich.bankcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.time.Duration;

@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private final TokenProperties accessToken;
    private final TokenProperties refreshToken;

    @ConstructorBinding
    public SecurityProperties(TokenProperties accessToken, TokenProperties refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public TokenProperties accessToken() {
        return accessToken;
    }

    public TokenProperties refreshToken() {
        return refreshToken;
    }

    public record TokenProperties(
            String jwk,
            Duration expiration
    ) {}

}
