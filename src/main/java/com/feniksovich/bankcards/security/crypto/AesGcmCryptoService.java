package com.feniksovich.bankcards.security.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class AesGcmCryptoService implements CryptoService {

    private final SecretKey aesKey;

    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;  // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits

    public AesGcmCryptoService(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public String encrypt(String plainInput) {
        Objects.requireNonNull(plainInput, "plainInput");

        if (plainInput.isEmpty()) {
            throw new IllegalArgumentException("plainInput cannot be empty");
        }

        try {
            // Generating a random IV
            final byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            // Initialize AES GCM cipher
            final Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmParameterSpec);

            final byte[] cipherBytes = cipher.doFinal(plainInput.getBytes(StandardCharsets.UTF_8));

            // Combine IV + cipherBytes + HMAC
            final byte[] composed = ByteBuffer.allocate(iv.length + cipherBytes.length)
                    .put(iv)
                    .put(cipherBytes)
                    .array();

            return Base64.getEncoder().encodeToString(composed);
        } catch (GeneralSecurityException ex) {
            throw new RuntimeException("Failed to perform data encryption", ex);
        }
    }

    @Override
    public String decrypt(String cipherInput) {
        Objects.requireNonNull(cipherInput, "cipherInput");

        if (cipherInput.isEmpty()) {
            throw new IllegalArgumentException("cipherInput cannot be empty");
        }

        final byte[] blob = Base64.getDecoder().decode(cipherInput);

        if (blob.length <= GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Ciphertext blob too short");
        }

        final byte[] iv = new byte[GCM_IV_LENGTH];
        final byte[] cipherBytes = new byte[blob.length - GCM_IV_LENGTH];
        System.arraycopy(blob, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(blob, GCM_IV_LENGTH, cipherBytes, 0, cipherBytes.length);

        try {
            final Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));
            final byte[] decryptedBytes = cipher.doFinal(cipherBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to perform data decryption", ex);
        }
    }
}
