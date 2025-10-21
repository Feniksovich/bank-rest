package com.feniksovich.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(5);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(90);

    @Test
    void signUp_201() throws Exception {
        final SignUpRequest req = SignUpRequest.builder()
                .lastName("Ivanov")
                .firstName("Ivan")
                .phoneNumber("9990000000")
                .password("very_secure_pass")
                .build();

        final Instant now = Instant.now();
        final AuthResponse resp = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .accessTokenExpiresAt(now.plus(ACCESS_TOKEN_EXPIRATION))
                .refreshTokenExpiresAt(now.plus(REFRESH_TOKEN_EXPIRATION))
                .build();

        when(authService.signUp(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.accessTokenExpiresAt").exists())
                .andExpect(jsonPath("$.accessTokenExpiresAt").isNotEmpty())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").exists())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresAt").isString())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").isString());

        verify(authService).signUp(any());
    }

    @Test
    void signIn_200() throws Exception {
        final SignInRequest req = SignInRequest.builder()
                .phoneNumber("9990000000")
                .password("very_secure_pass")
                .build();

        final Instant now = Instant.now();
        final AuthResponse resp = AuthResponse.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .accessTokenExpiresAt(now.plus(ACCESS_TOKEN_EXPIRATION))
                .refreshTokenExpiresAt(now.plus(REFRESH_TOKEN_EXPIRATION))
                .build();

        when(authService.signIn(any())).thenReturn(resp);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.accessTokenExpiresAt").exists())
                .andExpect(jsonPath("$.accessTokenExpiresAt").isNotEmpty())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").exists())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").isNotEmpty())
                .andExpect(jsonPath("$.accessTokenExpiresAt").isString())
                .andExpect(jsonPath("$.refreshTokenExpiresAt").isString());

        verify(authService).signIn(any());
    }

    @Test
    void signOut_200_withDefaultParameter() throws Exception {
        doNothing().when(authService).signOut(any(Boolean.class));

        mockMvc.perform(post("/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(authService).signOut(false);
    }

    @Test
    void signOut_200_withCustomParameter() throws Exception {
        doNothing().when(authService).signOut(any(Boolean.class));

        mockMvc.perform(post("/auth/signout")
                        .param("globally", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(authService).signOut(true);
    }
}


