package com.feniksovich.bankcards.security.factory;

import com.feniksovich.bankcards.config.SecurityProperties;
import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Getter
public class RefreshTokenFactory implements JwtTokenFactory {

    private final Duration expiration;

    private static final Set<GrantedAuthority> JWT_REFRESH_AUTHORITIES =
            Stream.of("jwt:refresh", "jwt:signout")
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toUnmodifiableSet());

    @Autowired
    public RefreshTokenFactory(SecurityProperties securityProperties) {
        this.expiration = securityProperties.refreshToken().expiration();
    }

    @Override
    public JwtToken generate(UserPrincipal principal) {
        final Instant now = Instant.now();
        return JwtToken.refreshToken(
                UUID.randomUUID(),
                principal.getId(),
                now,
                now.plus(expiration),
                JWT_REFRESH_AUTHORITIES
        );
    }
}
