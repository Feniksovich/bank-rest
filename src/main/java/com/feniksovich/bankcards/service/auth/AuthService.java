package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.dto.auth.AuthResponse;
import com.feniksovich.bankcards.dto.auth.SignInRequest;
import com.feniksovich.bankcards.dto.auth.SignUpRequest;

/**
 * Сервис аутентификации пользователей и управления токенами.
 */
public interface AuthService {
    /**
     * Регистрирует нового пользователя и выпускает пару токенов.
     *
     * @param request данные для регистрации
     * @return ответ с access и refresh токенами
     */
    AuthResponse signUp(SignUpRequest request);

    /**
     * Аутентифицирует пользователя и выпускает пару токенов.
     *
     * @param request данные для входа (логин/пароль)
     * @return ответ с access и refresh токенами
     */
    AuthResponse signIn(SignInRequest request);

    /**
     * Выполняет выход пользователя и инвалидирует
     * используемый refresh-токен(ы) из контекста безопасности.
     *
     * @param globally если true — инвалидировать все refresh-токены пользователя
     */
    void signOut(boolean globally);

    /**
     * Обновляет пару токенов с использованием
     * действующего refresh-токена из контекста безопасности.
     *
     * @return новая пара access и refresh токенов
     */
    AuthResponse refreshTokensPair();
}
