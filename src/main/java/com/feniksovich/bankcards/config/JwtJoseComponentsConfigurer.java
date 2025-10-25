package com.feniksovich.bankcards.config;

import com.feniksovich.bankcards.security.serialization.AccessTokenDeserializer;
import com.feniksovich.bankcards.security.serialization.AccessTokenSerializer;
import com.feniksovich.bankcards.security.serialization.RefreshTokenDeserializer;
import com.feniksovich.bankcards.security.serialization.RefreshTokenSerializer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;

/**
 * Конфигурация JOSE-компонентов для сериализации/десериализации JWT.
 */
@Configuration
public class JwtJoseComponentsConfigurer {

    private final SecurityProperties securityProperties;

    @Autowired
    public JwtJoseComponentsConfigurer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * Сериализатор access-токена на основе Json Web Signature.
     */
    @Bean
    public AccessTokenSerializer accessTokenJwsSerializer() throws JOSEException, ParseException {
        final OctetSequenceKey sequenceKey = OctetSequenceKey.parse(securityProperties.accessToken().jwk());
        final MACSigner signer = new MACSigner(sequenceKey);
        final JWSAlgorithm algorithm = JWSAlgorithm.parse(sequenceKey.getAlgorithm().getName());

        return new AccessTokenSerializer(signer, algorithm);
    }

    /**
     * Десериализатор access-токена на основе Json Web Signature.
     */
    @Bean
    public AccessTokenDeserializer accessTokenJwsDeserializer() throws JOSEException, ParseException {
        final OctetSequenceKey sequenceKey = OctetSequenceKey.parse(securityProperties.accessToken().jwk());
        final MACVerifier verifier = new MACVerifier(sequenceKey);

        return new AccessTokenDeserializer(verifier);
    }

    /**
     * Сериализатор refresh-токена на основе Json Web Encryption.
     */
    @Bean
    public RefreshTokenSerializer refreshTokenJweSerializer() throws JOSEException, ParseException {
        final OctetSequenceKey sequenceKey = OctetSequenceKey.parse(securityProperties.refreshToken().jwk());
        final AESEncrypter encrypter = new AESEncrypter(sequenceKey);
        final JWEAlgorithm algorithm = JWEAlgorithm.parse(sequenceKey.getAlgorithm().getName());

        return new RefreshTokenSerializer(encrypter, algorithm);
    }

    /**
     * Десериализатор refresh-токена на основе Json Web Encryption.
     */
    @Bean
    public RefreshTokenDeserializer refreshTokenJweDeserializer() throws JOSEException, ParseException {
        final OctetSequenceKey sequenceKey = OctetSequenceKey.parse(securityProperties.refreshToken().jwk());
        final AESDecrypter decrypter = new AESDecrypter(sequenceKey);

        return new RefreshTokenDeserializer(decrypter);
    }
}
