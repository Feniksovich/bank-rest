package com.feniksovich.bankcards.service.card;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Сервис управления банковскими картами.
 */
public interface CardService {

    // Admin Operations

    /**
     * Возвращает данные карты по идентификатору.
     *
     * @param cardId идентификатор карты
     * @return данные карты
     */
    CardData getById(UUID cardId);

    /**
     * Создает новую карту для пользователя.
     *
     * @param userId идентификатор пользователя
     * @return данные созданной карты
     */
    CardData create(UUID userId);

    /**
     * Удаляет карту.
     *
     * @param cardId идентификатор карты
     */
    void delete(UUID cardId);

    /**
     * Возвращает постраничный список всех карт.
     *
     * @param pageable параметры пагинации
     * @return страница с картами
     */
    Page<CardData> getAll(Pageable pageable);

    /**
     * Устанавливает состояние блокировки карты.
     *
     * @param cardId  идентификатор карты
     * @param blocked true — заблокировать, false — разблокировать
     */
    void setBlockedById(UUID cardId, boolean blocked);

    // User Operations

    /**
     * Возвращает карту пользователя по идентификатору.
     *
     * @param userId идентификатор владельца карты
     * @param cardId идентификатор карты
     * @return данные карты
     */
    CardData getOwnById(UUID userId, UUID cardId);

    /**
     * Возвращает список карт пользователя с пагинацией.
     *
     * @param userId   идентификатор владельца карт
     * @param pageable параметры пагинации
     * @return страница с картами
     */
    Page<CardData> getAllOwned(UUID userId, Pageable pageable);

    /**
     * Устанавливает состояние блокировки карты, проверяя владельца.
     *
     * @param userId  идентификатор владельца
     * @param cardId  идентификатор карты
     * @param blocked true — заблокировать, false — разблокировать
     */
    void setBlockedOwnById(UUID userId, UUID cardId, boolean blocked);

    /**
     * Выполняет перевод между картами пользователя.
     *
     * @param userId  идентификатор владельца
     * @param request параметры перевода
     */
    void performTransaction(UUID userId, TransactionRequest request);
}
