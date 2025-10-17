package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;

public interface AuthService {
    AuthResponse signUp(SignUpRequest request);
    AuthResponse signIn(SignInRequest request);
    void signOut(boolean globally);
    AuthResponse refreshTokensPair();
}
