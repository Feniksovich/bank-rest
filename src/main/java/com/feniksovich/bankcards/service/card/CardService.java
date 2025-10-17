package com.feniksovich.bankcards.service.card;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardService {

    // Admin Operations

    CardData getById(UUID cardId);
    CardData create(UUID userId);
    void delete(UUID cardId);
    Page<CardData> getAll(Pageable pageable);
    void setBlockedById(UUID cardId, boolean blocked);

    // User Operations

    CardData getOwnById(UUID userId, UUID cardId);
    Page<CardData> getAllOwned(UUID userId, Pageable pageable);
    void setBlockedOwnById(UUID userId, UUID cardId, boolean blocked);
    void performTransaction(UUID userId, TransactionRequest request);
}
