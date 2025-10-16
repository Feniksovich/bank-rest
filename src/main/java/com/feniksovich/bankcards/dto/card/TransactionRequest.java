package com.feniksovich.bankcards.dto.card;

import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotBlank
    @Pattern(regexp = RegexPatterns.CARD_PAN_CHUNK)
    private String fromPanLast4;

    @NotBlank
    @Pattern(regexp = RegexPatterns.CARD_PAN_CHUNK)
    private String toPanLast4;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}
