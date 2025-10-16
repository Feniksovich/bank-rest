package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.TokensPairResponse;
import com.feniksovich.bankcards.dto.user.SignInRequest;
import com.feniksovich.bankcards.dto.user.SignUpRequest;
import com.feniksovich.bankcards.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TokensPairResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TokensPairResponse signIn(@RequestBody @Valid SignInRequest request) {
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
    public TokensPairResponse refreshTokens() {
        return authService.refreshTokensPair();
    }
}
