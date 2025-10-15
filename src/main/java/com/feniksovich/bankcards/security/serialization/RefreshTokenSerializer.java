package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class RefreshTokenSerializer implements JwtTokenSerializer {

    private final JWEEncrypter encrypter;
    private final JWEAlgorithm algorithm;

    private static final EncryptionMethod ENCRYPTION_METHOD = EncryptionMethod.A256GCM;
    private static final String AUTHORITIES_CLAIM = "authorities";

    public RefreshTokenSerializer(AESEncrypter encrypter, JWEAlgorithm algorithm) {
        this.encrypter = encrypter;
        this.algorithm = algorithm;
    }

    @Override
    public String serialize(JwtToken jwtToken) {
        final Set<String> authorities = jwtToken.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        final JWEHeader header = new JWEHeader(algorithm, ENCRYPTION_METHOD);
        final JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(jwtToken.id().toString())
                .subject(String.valueOf(jwtToken.userId()))
                .issueTime(Date.from(jwtToken.createdAt()))
                .expirationTime(Date.from(jwtToken.expiresAt()))
                .claim(AUTHORITIES_CLAIM, authorities)
                .build();

        try {
            final EncryptedJWT encryptedJwt = new EncryptedJWT(header, claims);
            encryptedJwt.encrypt(encrypter);
            return encryptedJwt.serialize();
        } catch (Exception ex) {
            // Failed to encrypt the JWT
            throw new RuntimeException("Failed to serialize refresh token", ex);
        }
    }
}
