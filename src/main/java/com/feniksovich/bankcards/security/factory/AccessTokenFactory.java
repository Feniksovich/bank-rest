package com.feniksovich.bankcards.security.factory;

import com.feniksovich.bankcards.config.SecurityProperties;
import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Component
public class AccessTokenFactory implements JwtTokenFactory {

    private final Duration expiration;

    @Autowired
    public AccessTokenFactory(SecurityProperties securityProperties) {
        this.expiration = securityProperties.accessToken().expiration();
    }

    @Override
    public JwtToken generate(UserPrincipal principal) {
        final Instant now = Instant.now();
        return JwtToken.accessToken(
                UUID.randomUUID(),
                principal.getId(),
                now,
                now.plus(expiration),
                principal.getAuthorities()
        );
    }
}
