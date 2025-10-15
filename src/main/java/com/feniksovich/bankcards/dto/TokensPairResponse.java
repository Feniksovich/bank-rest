package com.feniksovich.bankcards.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TokensPairResponse {

    private String accessToken;
    private String refreshToken;
    private String expiresAt;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @SuppressWarnings("unused")
    TokensPairResponse() {
        // No-arg constructor required
    }

    public TokensPairResponse(String accessToken, String refreshToken, Instant expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = DATE_TIME_FORMATTER.format(expiresAt.atZone(ZoneId.systemDefault()));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "TokensPairResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                '}';
    }
}
