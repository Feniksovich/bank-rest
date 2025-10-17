package com.feniksovich.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardData {
    private UUID id;
    private String panLast4;
    private LocalDate expiresAt;
    private String cardHolder;
    private BigDecimal balance;
    private boolean blocked;
}
