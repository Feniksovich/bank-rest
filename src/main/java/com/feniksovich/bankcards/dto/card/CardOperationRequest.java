package com.feniksovich.bankcards.dto.card;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardOperationRequest {
    @NotNull
    private UUID userId;

    @NotBlank
    @Pattern(regexp = RegexPatterns.CARD_PAN_CHUNK)
    private String panLast4;
}
