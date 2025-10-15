package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.TokensPairResponse;
import com.feniksovich.bankcards.dto.user.SignInRequest;
import com.feniksovich.bankcards.dto.user.SignUpRequest;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.security.JwtAuthenticationToken;
import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.security.factory.AccessTokenFactory;
import com.feniksovich.bankcards.security.factory.RefreshTokenFactory;
import com.feniksovich.bankcards.security.serialization.AccessTokenSerializer;
import com.feniksovich.bankcards.security.serialization.RefreshTokenSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRefreshTokenService userRefreshTokenService;
    private final AuthenticationManager authenticationManager;

    private final AccessTokenFactory accessTokenFactory;
    private final RefreshTokenFactory refreshTokenFactory;

    private final AccessTokenSerializer accessTokenSerializer;
    private final RefreshTokenSerializer refreshTokenSerializer;

    @Autowired
    public AuthServiceImpl(
            UserService userService,
            UserRefreshTokenService userRefreshTokenService,
            AuthenticationManager authenticationManager,
            AccessTokenFactory accessTokenFactory,
            RefreshTokenFactory refreshTokenFactory,
            AccessTokenSerializer accessTokenSerializer,
            RefreshTokenSerializer refreshTokenSerializer
    ) {
        this.userService = userService;
        this.userRefreshTokenService = userRefreshTokenService;
        this.authenticationManager = authenticationManager;
        this.accessTokenFactory = accessTokenFactory;
        this.refreshTokenFactory = refreshTokenFactory;
        this.accessTokenSerializer = accessTokenSerializer;
        this.refreshTokenSerializer = refreshTokenSerializer;
    }

    @Override
    public TokensPairResponse signUp(SignUpRequest request) {
        final User user = userService.createUser(request);
        final UserPrincipal principal = UserPrincipal.of(user);
        return issueTokensPair(principal);
    }

    @Override
    public TokensPairResponse signIn(SignInRequest request) {
        final UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword());
        final Authentication authentication = authenticationManager.authenticate(token);
        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return issueTokensPair(principal);
    }

    @Override
    public void signOut(boolean globally) {
        // JwtAuthenticationProvider stores authentication in SecurityContextHolder
        final JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (globally) {
            userRefreshTokenService.invalidateAll(authentication.getPrincipal().getId());
        } else {
            userRefreshTokenService.invalidate(authentication.getCredentials());
        }

        SecurityContextHolder.clearContext();
    }

    @Override
    public TokensPairResponse refreshTokensPair() {
        // JwtAuthenticationProvider stores authentication in SecurityContextHolder
        final JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        // Force invalidate used refresh token
        userRefreshTokenService.invalidate(authentication.getCredentials());

        final UserPrincipal principal = authentication.getPrincipal();
        return issueTokensPair(principal);
    }

    private TokensPairResponse issueTokensPair(UserPrincipal principal) {
        final JwtToken accessToken = accessTokenFactory.generate(principal);
        final JwtToken refreshToken = refreshTokenFactory.generate(principal);

        userRefreshTokenService.track(refreshToken);

        return new TokensPairResponse(
                accessTokenSerializer.serialize(accessToken),
                refreshTokenSerializer.serialize(refreshToken),
                accessToken.expiresAt()
        );
    }

}
