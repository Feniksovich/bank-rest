package com.feniksovich.bankcards.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtToken jwtToken;
    private final UserPrincipal principal;

    private JwtAuthenticationToken(JwtToken jwtToken, UserPrincipal principal, boolean authenticated) {
        super(jwtToken.authorities());
        this.jwtToken = jwtToken;
        this.principal = principal;
        setAuthenticated(authenticated);
    }

    public static JwtAuthenticationToken unauthenticated(JwtToken jwtToken) {
        return new JwtAuthenticationToken(jwtToken, null, false);
    }

    public static JwtAuthenticationToken authenticated(JwtToken jwtToken, UserPrincipal principal) {
        return new JwtAuthenticationToken(jwtToken, principal, true);
    }

    @Override
    public JwtToken getCredentials() {
        return jwtToken;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwtToken, principal);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JwtAuthenticationToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(jwtToken, that.jwtToken)
                && Objects.equals(principal, that.principal);
    }
}
