package com.feniksovich.bankcards.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

/**
 * Расширенный сервис загрузки пользователей для Spring Security.
 */
public interface ExtendedUserDetailsService extends UserDetailsService {
    /**
     * Загружает пользователя UUID.
     */
    UserDetails loadUserById(UUID id) throws UsernameNotFoundException;
}
