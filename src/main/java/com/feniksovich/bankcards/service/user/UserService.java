package com.feniksovich.bankcards.service.user;

import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Сервис управления пользователями.
 */
public interface UserService {
    /**
     * Регистрирует нового пользователя.
     *
     * @param request данные для регистрации
     * @return данные созданного пользователя
     */
    UserData register(SignUpRequest request);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return данные пользователя
     */
    UserData getById(UUID id);

    /**
     * Возвращает пользователя по номеру телефона.
     *
     * @param phoneNumber номер телефона
     * @return данные пользователя
     */
    UserData getByPhoneNumber(String phoneNumber);

    /**
     * Возвращает постраничный список пользователей.
     *
     * @param pageable параметры пагинации
     * @return страница с пользователями
     */
    Page<UserData> getAll(Pageable pageable);

    /**
     * Обновляет пользователя по идентификатору.
     *
     * @param id      идентификатор пользователя
     * @param request данные для обновления
     */
    void updateById(UUID id, UserUpdateRequest request);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     */
    void deleteById(UUID id);
}
