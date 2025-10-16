package com.feniksovich.bankcards.service.card;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CardService {
    CardData create(UUID userId);
    Page<CardData> getAll(Pageable pageable);
    Page<CardData> getAllByUserId(UUID userId, Pageable pageable);

    CardData getById(UUID userId, UUID cardId);
    CardData getByPanLast4(UUID userId, String panLast4);

    void setStatusById(UUID userId, UUID cardId, CardStatus status);
    void setStatusByPanLast4(UUID userId, String panLast4, CardStatus status);

    void performTransaction(UUID userId, TransactionRequest request);

    void deleteByPanLast4(UUID userId, String panLast4);
}
