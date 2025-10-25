package com.feniksovich.bankcards.security;

import com.feniksovich.bankcards.security.serialization.AccessTokenDeserializer;
import com.feniksovich.bankcards.security.serialization.RefreshTokenDeserializer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

/**
 * Конвертер строковых JWT-токенов в {@link JwtAuthenticationToken},
 * переданных HTTP заголовке Authorization по схеме Bearer.
 * Реализован как компонент Spring Security.
 */
@Component
public class BearerJwtAuthenticationTokenConverter implements AuthenticationConverter {

    private final AccessTokenDeserializer accessTokenDeserializer;
    private final RefreshTokenDeserializer refreshTokenDeserializer;

    private static final String BEARER_SCHEMA_HEAD = "Bearer ";

    @Autowired
    public BearerJwtAuthenticationTokenConverter(
            AccessTokenDeserializer accessTokenDeserializer,
            RefreshTokenDeserializer refreshTokenDeserializer
    ) {
        this.accessTokenDeserializer = accessTokenDeserializer;
        this.refreshTokenDeserializer = refreshTokenDeserializer;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(BEARER_SCHEMA_HEAD)) {
            final String token = authorization.substring(BEARER_SCHEMA_HEAD.length()).trim();

            final JwtToken accessToken = accessTokenDeserializer.deserialize(token);
            if (accessToken != null) {
                return JwtAuthenticationToken.unauthenticated(accessToken);
            }

            final JwtToken refreshToken = refreshTokenDeserializer.deserialize(token);
            if (refreshToken != null) {
                return JwtAuthenticationToken.unauthenticated(refreshToken);
            }

            throw new BadCredentialsException("Invalid token");
        }
        return null;
    }

}
