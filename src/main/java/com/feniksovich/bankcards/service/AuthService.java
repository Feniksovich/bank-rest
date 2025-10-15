package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.TokensPairResponse;
import com.feniksovich.bankcards.dto.user.SignInRequest;
import com.feniksovich.bankcards.dto.user.SignUpRequest;

public interface AuthService {
    TokensPairResponse signUp(SignUpRequest request);
    TokensPairResponse signIn(SignInRequest request);
    void signOut(boolean globally);
    TokensPairResponse refreshTokensPair();
}
