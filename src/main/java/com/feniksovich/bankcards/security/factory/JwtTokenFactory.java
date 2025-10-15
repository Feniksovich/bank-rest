package com.feniksovich.bankcards.security.factory;

import com.feniksovich.bankcards.security.JwtToken;
import com.feniksovich.bankcards.security.UserPrincipal;

public interface JwtTokenFactory {
    JwtToken generate(UserPrincipal principal);
}
