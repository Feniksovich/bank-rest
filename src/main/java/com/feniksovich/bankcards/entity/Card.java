package com.feniksovich.bankcards.entity;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "cards") //TODO Constrains, indexes
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "pan_encrypted", nullable = false, length = 512)
    private String panEncrypted;

    @Pattern(regexp = RegexPatterns.CARD_PAN_CHUNK)
    @Column(name = "pan_last_chunk", nullable = false, length = 4)
    private String panLast4;

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "blocked", nullable = false)
    private boolean blocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cards_user"))
    private User user;

    @PrePersist
    @PreUpdate
    private void normalizeExpirationDate() {
        if (expiresAt != null) {
            expiresAt = expiresAt.withDayOfMonth(1);
        }
    }
}