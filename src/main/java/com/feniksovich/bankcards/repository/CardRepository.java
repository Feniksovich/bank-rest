package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностями {@link Card}.
 */
public interface CardRepository extends JpaRepository<Card, UUID> {
    /**
     * Находит все карты пользователя с пагинацией.
     *
     * @param userId   идентификатор пользователя
     * @param pageable параметры пагинации
     * @return страница с картами
     */
    Page<Card> findAllByUserId(UUID userId, Pageable pageable);

    /**
     * Находит карту по идентификатору пользователя и идентификатору карты.
     *
     * @param userId идентификатор пользователя
     * @param cardId идентификатор карты
     * @return карта
     */
    Optional<Card> findByUserIdAndId(UUID userId, UUID cardId);

    /**
     * Находит карту по идентификатору пользователя и последним 4 цифрам PAN.
     *
     * @param userId  идентификатор пользователя
     * @param panLast4 последние 4 цифры PAN
     * @return карта
     */
    Optional<Card> findByUserIdAndPanLast4(UUID userId, String panLast4);

    /**
     * Проверяет существование карты по пользователю и последним 4 цифрам PAN.
     *
     * @param userId  идентификатор пользователя
     * @param panLast4 последние 4 цифры PAN
     * @return true, если карта существует
     */
    boolean existsByUserIdAndPanLast4(UUID userId, String panLast4);
}
