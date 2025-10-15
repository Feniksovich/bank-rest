package com.feniksovich.bankcards.security.serialization;

import com.feniksovich.bankcards.security.JwtToken;

public interface JwtTokenDeserializer {
    JwtToken deserialize(String token);
}
