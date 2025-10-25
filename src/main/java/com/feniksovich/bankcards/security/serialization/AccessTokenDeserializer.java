package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Десериализация и верификация access-токенов с помощью JWS.
 */
public class AccessTokenDeserializer implements JwtTokenDeserializer {

    private final JWSVerifier verifier;

    public AccessTokenDeserializer(JWSVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public JwtToken deserialize(String token) {
        try {
            final SignedJWT signedJwt = SignedJWT.parse(token);

            if (!signedJwt.verify(verifier)) {
                return null;
            }

            final JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
            final Set<SimpleGrantedAuthority> authorities = claims.getStringListClaim("authorities")
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            return JwtToken.accessToken(
                    UUID.fromString(claims.getJWTID()),
                    UUID.fromString(claims.getSubject()),
                    claims.getIssueTime().toInstant(),
                    claims.getExpirationTime().toInstant(),
                    authorities
            );
        } catch (ParseException ex) {
            // Failed to parse token, provided invalid one
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deserialize access token", ex);
        }
    }
}
