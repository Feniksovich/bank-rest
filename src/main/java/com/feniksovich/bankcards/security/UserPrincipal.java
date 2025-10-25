package com.feniksovich.bankcards.security;

import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

/**
 * Реализация UserDetails для аутентифицированного пользователя.
 */
@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String phoneNumber;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Создает principal из сущности пользователя.
     */
    public static UserPrincipal of(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.getRole().getAuthorities()
        );
    }

    /**
     * Создает principal из DTO пользователя (без пароля).
     */
    public static UserPrincipal of(UserData userData) {
        return new UserPrincipal(
                userData.getId(),
                userData.getPhoneNumber(),
                null,
                userData.getRole().getAuthorities()
        );
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }
}
