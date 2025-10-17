package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessTokenSerializer implements JwtTokenSerializer {

    private final JWSSigner signer;
    private final JWSAlgorithm algorithm;

    public AccessTokenSerializer(JWSSigner signer, JWSAlgorithm algorithm) {
        this.signer = signer;
        this.algorithm = algorithm;
    }

    @Override
    public String serialize(JwtToken jwtToken) {
        final Set<String> authorities = jwtToken.authorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        final JWSHeader header = new JWSHeader(algorithm);
        final JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(jwtToken.id().toString())
                .subject(String.valueOf(jwtToken.userId()))
                .issueTime(Date.from(jwtToken.createdAt()))
                .expirationTime(Date.from(jwtToken.expiresAt()))
                .claim("authorities", authorities)
                .build();

        try {
            final SignedJWT signedJwt = new SignedJWT(header, claims);
            signedJwt.sign(signer);
            return signedJwt.serialize();
        } catch (Exception ex) {
            // Failed to sign the JWT
            throw new RuntimeException("Failed to serialize access token", ex);
        }
    }

}
