package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.dto.TokensPairResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;

public interface AuthService {
    TokensPairResponse signUp(SignUpRequest request);
    TokensPairResponse signIn(SignInRequest request);
    void signOut(boolean globally);
    TokensPairResponse refreshTokensPair();
}
