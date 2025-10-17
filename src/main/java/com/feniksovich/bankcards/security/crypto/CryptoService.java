package com.feniksovich.bankcards.security.crypto;

public interface CryptoService {
    String encrypt(String plainInput);
    String decrypt(String cipherInput);
}
