package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for user registration, login and token management")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "User registration", description = "Creates a new user and returns access and refresh tokens pair")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User with phone number specified already exists")
    })
    public AuthResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "User login", description = "Authenticates user and returns access and refresh tokens pair")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authentication"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public AuthResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('jwt:signout')")
    @Operation(summary = "User logout", description = "Logs out user and invalidates refresh token")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Authentication with refresh token required")
    })
    public void signOut(@RequestParam(required = false, defaultValue = "false") boolean globally) {
        authService.signOut(globally);
    }

    @PostMapping("/tokens")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('jwt:refresh')")
    @Operation(summary = "Refresh tokens", description = "Refreshes access and refresh tokens pair using refresh token")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens pair successfully refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public AuthResponse refreshTokens() {
        return authService.refreshTokensPair();
    }
}
