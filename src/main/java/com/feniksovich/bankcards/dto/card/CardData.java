package com.feniksovich.bankcards.dto.card;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardData {
    private UUID id;
    private String panLast4;

    @JsonSerialize(using = CardExpirationDateSerializer.class)
    private LocalDate expiresAt;

    private String cardHolder;
    private BigDecimal balance;
    private boolean blocked;
}
