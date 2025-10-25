package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с сущностями {@link com.feniksovich.bankcards.entity.UserRefreshToken}.
 */
@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, UUID> {
    /**
     * Удаляет все refresh-токены, связанные с пользователем.
     *
     * @param userId идентификатор пользователя
     */
    void deleteAllByUserId(UUID userId);
}
