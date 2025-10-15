package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
}
