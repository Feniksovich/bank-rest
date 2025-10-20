package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.entity.Role;
import com.feniksovich.bankcards.security.JwtAuthenticationToken;
import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.security.factory.AccessTokenFactory;
import com.feniksovich.bankcards.security.factory.RefreshTokenFactory;
import com.feniksovich.bankcards.security.serialization.AccessTokenSerializer;
import com.feniksovich.bankcards.security.serialization.RefreshTokenSerializer;
import com.feniksovich.bankcards.service.auth.AuthServiceImpl;
import com.feniksovich.bankcards.service.auth.UserRefreshTokenService;
import com.feniksovich.bankcards.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRefreshTokenService userRefreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AccessTokenFactory accessTokenFactory;

    @Mock
    private RefreshTokenFactory refreshTokenFactory;

    @Mock
    private AccessTokenSerializer accessTokenSerializer;

    @Mock
    private RefreshTokenSerializer refreshTokenSerializer;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserData userData;
    private UserPrincipal userPrincipal;
    private JwtToken accessToken;
    private JwtToken refreshToken;
    private JwtAuthenticationToken jwtAuthenticationToken;

    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(5);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(90);
    private static final List<GrantedAuthority> USER_ROLE_AUTHORITIES =
            List.of(new SimpleGrantedAuthority("ROLE_USER"));

    @BeforeEach
    void setup() {
        final UUID userId = UUID.randomUUID();
        final Instant now = Instant.now();
        final Instant accessExpiresAt = now.plus(ACCESS_TOKEN_EXPIRATION);
        final Instant refreshExpiresAt = now.plus(REFRESH_TOKEN_EXPIRATION);

        userData = UserData.builder()
                .id(userId)
                .phoneNumber("1234567890")
                .firstName("Иван")
                .lastName("Иванов")
                .role(Role.USER)
                .build();

        userPrincipal = UserPrincipal.of(userData);

        accessToken = JwtToken.accessToken(
                UUID.randomUUID(),
                userId,
                now,
                accessExpiresAt,
                USER_ROLE_AUTHORITIES
        );

        refreshToken = JwtToken.refreshToken(
                UUID.randomUUID(),
                userId,
                now,
                refreshExpiresAt,
                USER_ROLE_AUTHORITIES
        );

        jwtAuthenticationToken = JwtAuthenticationToken.authenticated(refreshToken, userPrincipal);
    }

    @Test
    void signUp_WhenValidRequest_ShouldReturnAuthResponse() {
        final SignUpRequest request = SignUpRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("1234567890")
                .password("plainPassword")
                .build();

        final AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("serializedAccessToken")
                .refreshToken("serializedRefreshToken")
                .accessTokenExpiresAt(accessToken.expiresAt())
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .build();

        when(userService.register(request)).thenReturn(userData);
        when(accessTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(accessToken);
        when(refreshTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(refreshToken);
        when(accessTokenSerializer.serialize(accessToken)).thenReturn("serializedAccessToken");
        when(refreshTokenSerializer.serialize(refreshToken)).thenReturn("serializedRefreshToken");

        final AuthResponse result = authService.signUp(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userService).register(request);
        verify(accessTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(refreshTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(accessTokenSerializer).serialize(accessToken);
        verify(refreshTokenSerializer).serialize(refreshToken);
        verify(userRefreshTokenService).track(refreshToken);
    }

    @Test
    void signIn_WhenValidCredentials_ShouldReturnAuthResponse() {
        final SignInRequest request = SignInRequest.builder()
                .phoneNumber("1234567890")
                .password("password")
                .build();

        final UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword());

        final Authentication authentication = mock(Authentication.class);
        final AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("serializedAccessToken")
                .refreshToken("serializedRefreshToken")
                .accessTokenExpiresAt(accessToken.expiresAt())
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .build();

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(accessTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(accessToken);
        when(refreshTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(refreshToken);
        when(accessTokenSerializer.serialize(accessToken)).thenReturn("serializedAccessToken");
        when(refreshTokenSerializer.serialize(refreshToken)).thenReturn("serializedRefreshToken");

        final AuthResponse result = authService.signIn(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(authenticationManager).authenticate(authToken);
        verify(accessTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(refreshTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(accessTokenSerializer).serialize(accessToken);
        verify(refreshTokenSerializer).serialize(refreshToken);
        verify(userRefreshTokenService).track(refreshToken);
    }

    @Test
    void signOut_WhenGloballyTrue_ShouldInvalidateAllTokens() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);

        authService.signOut(true);

        verify(userRefreshTokenService).invalidateAll(userPrincipal.getId());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void signOut_WhenGloballyFalse_ShouldInvalidateCurrentToken() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);

        authService.signOut(false);

        verify(userRefreshTokenService).invalidate(refreshToken);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void refreshTokensPair_WhenValidRefreshToken_ShouldReturnNewTokens() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);

        final JwtToken newAccessToken = JwtToken.accessToken(
                UUID.randomUUID(),
                userPrincipal.getId(),
                Instant.now(),
                Instant.now().plus(ACCESS_TOKEN_EXPIRATION),
                USER_ROLE_AUTHORITIES
        );

        final JwtToken newRefreshToken = JwtToken.refreshToken(
                UUID.randomUUID(),
                userPrincipal.getId(),
                Instant.now(),
                Instant.now().plus(REFRESH_TOKEN_EXPIRATION),
                USER_ROLE_AUTHORITIES
        );

        final AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("newSerializedAccessToken")
                .refreshToken("newSerializedRefreshToken")
                .accessTokenExpiresAt(newAccessToken.expiresAt())
                .refreshTokenExpiresAt(newRefreshToken.expiresAt())
                .build();

        when(accessTokenFactory.generate(userPrincipal)).thenReturn(newAccessToken);
        when(refreshTokenFactory.generate(userPrincipal)).thenReturn(newRefreshToken);
        when(accessTokenSerializer.serialize(newAccessToken)).thenReturn("newSerializedAccessToken");
        when(refreshTokenSerializer.serialize(newRefreshToken)).thenReturn("newSerializedRefreshToken");

        final AuthResponse result = authService.refreshTokensPair();

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRefreshTokenService).invalidate(refreshToken);
        verify(accessTokenFactory).generate(userPrincipal);
        verify(refreshTokenFactory).generate(userPrincipal);
        verify(accessTokenSerializer).serialize(newAccessToken);
        verify(refreshTokenSerializer).serialize(newRefreshToken);
        verify(userRefreshTokenService).track(newRefreshToken);
    }

    @Test
    void issueTokensPair_WhenValidPrincipal_ShouldReturnAuthResponse() {
        final AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("serializedAccessToken")
                .refreshToken("serializedRefreshToken")
                .accessTokenExpiresAt(accessToken.expiresAt())
                .refreshTokenExpiresAt(refreshToken.expiresAt())
                .build();

        final SignUpRequest request = SignUpRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("1234567890")
                .password("plainPassword")
                .build();

        when(userService.register(request)).thenReturn(userData);
        when(accessTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(accessToken);
        when(refreshTokenFactory.generate(argThat(p -> p.getId().equals(userData.getId())))).thenReturn(refreshToken);
        when(accessTokenSerializer.serialize(accessToken)).thenReturn("serializedAccessToken");
        when(refreshTokenSerializer.serialize(refreshToken)).thenReturn("serializedRefreshToken");

        final AuthResponse result = authService.signUp(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userService).register(request);
        verify(accessTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(refreshTokenFactory).generate(argThat(p -> p.getId().equals(userData.getId())));
        verify(accessTokenSerializer).serialize(accessToken);
        verify(refreshTokenSerializer).serialize(refreshToken);
        verify(userRefreshTokenService).track(refreshToken);
    }
}