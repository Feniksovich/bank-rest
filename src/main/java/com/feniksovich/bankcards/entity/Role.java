package com.feniksovich.bankcards.entity;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final Set<SimpleGrantedAuthority> authorities;

    Role(String... authorities) {
        this.authorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}