package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Десериализация и расшифровка refresh-токенов c помощью JWE.
 */
public class RefreshTokenDeserializer implements JwtTokenDeserializer {

    private final JWEDecrypter decrypter;

    public RefreshTokenDeserializer(AESDecrypter decrypter) {
        this.decrypter = decrypter;
    }

    @Override
    public JwtToken deserialize(String token) {
        try {
            final EncryptedJWT encryptedJwt = EncryptedJWT.parse(token);
            encryptedJwt.decrypt(decrypter);

            final JWTClaimsSet claims = encryptedJwt.getJWTClaimsSet();
            final Set<SimpleGrantedAuthority> authorities = claims.getStringListClaim("authorities")
                    .stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());

            return JwtToken.refreshToken(
                    UUID.fromString(claims.getJWTID()),
                    UUID.fromString(claims.getSubject()),
                    claims.getIssueTime().toInstant(),
                    claims.getExpirationTime().toInstant(),
                    authorities
            );
        } catch (ParseException | JOSEException ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to deserialize access token", ex);
        }
    }
}
