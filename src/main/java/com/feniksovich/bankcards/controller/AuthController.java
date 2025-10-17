package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('jwt:signout')")
    public void signOut(@RequestParam(required = false, defaultValue = "false") boolean globally) {
        authService.signOut(globally);
    }

    @PostMapping("/tokens")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('jwt:refresh')")
    public AuthResponse refreshTokens() {
        return authService.refreshTokensPair();
    }
}
