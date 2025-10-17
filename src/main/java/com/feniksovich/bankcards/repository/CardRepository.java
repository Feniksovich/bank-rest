package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findAllByUserId(UUID userId, Pageable pageable);
    Optional<Card> findByUserIdAndId(UUID userId, UUID cardId);
    Optional<Card> findByUserIdAndPanLast4(UUID userId, String panLast4);
}
