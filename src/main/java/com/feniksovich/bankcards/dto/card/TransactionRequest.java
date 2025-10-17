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
    @NotBlank(message = "Source card PAN last 4 digits required")
    @Pattern(
            regexp = RegexPatterns.CARD_PAN_CHUNK,
            message = "Source card PAN last 4 digits format is invalid"
    )
    private String fromPanLast4;

    @NotBlank(message = "Destination card PAN last 4 digits required")
    @Pattern(
            regexp = RegexPatterns.CARD_PAN_CHUNK,
            message = "Destination card PAN last 4 digits format is invalid"
    )
    private String toPanLast4;

    @NotNull
    @DecimalMin(value = "0.01", message = "Transaction amount must be at least 0.01")
    private BigDecimal amount;
}
