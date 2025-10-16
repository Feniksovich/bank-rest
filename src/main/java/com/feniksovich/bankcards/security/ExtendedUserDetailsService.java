package com.feniksovich.bankcards.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface ExtendedUserDetailsService extends UserDetailsService {
    UserDetails loadUserById(UUID id) throws UsernameNotFoundException;
}
