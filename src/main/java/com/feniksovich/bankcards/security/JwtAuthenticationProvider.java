package com.feniksovich.bankcards.security;

import com.feniksovich.bankcards.service.auth.UserRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final ExtendedUserDetailsService userDetailsService;
    private final UserRefreshTokenService userRefreshTokenService;

    @Autowired
    public JwtAuthenticationProvider(
            ExtendedUserDetailsService userDetailsService,
            UserRefreshTokenService userRefreshTokenService
    ) {
        this.userDetailsService = userDetailsService;
        this.userRefreshTokenService = userRefreshTokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final JwtToken jwtToken = ((JwtAuthenticationToken) authentication).getCredentials();

        if (jwtToken.isExpired()) {
            throw new CredentialsExpiredException("Token expired");
        }

        if (jwtToken.type() == TokenType.REFRESH && !userRefreshTokenService.isTracked(jwtToken)) {
            throw new BadCredentialsException("Token invalid");
        }

        final UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserById(jwtToken.userId());
        return JwtAuthenticationToken.authenticated(jwtToken, principal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
