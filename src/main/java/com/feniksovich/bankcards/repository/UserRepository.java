package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностями {@link User}.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Находит пользователя по номеру телефона.
     *
     * @param phoneNumber номер телефона
     * @return опционал с пользователем
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Проверяет существование пользователя с указанным номером телефона.
     *
     * @param phoneNumber номер телефона
     * @return true, если пользователь существует
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
