package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardCreateRequest;
import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/cards")
@Tag(name = "Card Management", description = "API for managing bank cards (admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all cards", description = "Returns paginated list of all cards in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards list successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Page<CardData> getAllCards(@PageableDefault Pageable pageable) {
        return cardService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search cards by user ID", description = "Returns cards belonging to a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User cards successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public Page<CardData> searchCardsByUserId(@RequestParam UUID userId, @PageableDefault Pageable pageable) {
        return cardService.getAllOwned(userId, pageable);
    }

    @GetMapping("/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get card by ID", description = "Returns data of a specific card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card data successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public CardData getCard(@PathVariable UUID cardId) {
        return cardService.getById(cardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new card", description = "Creates a new card for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public CardData createCard(@RequestBody @Valid CardCreateRequest request) {
        return cardService.create(request.getUserId());
    }

    @PostMapping("/{cardId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Activate card", description = "Activates a blocked card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully activated"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public void activateCard(@PathVariable UUID cardId) {
        cardService.setBlockedById(cardId, false);
    }

    @PostMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Block card", description = "Blocks a card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully blocked"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public void blockCard(@PathVariable UUID cardId) {
        cardService.setBlockedById(cardId, true);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete card", description = "Deletes a card from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public void deleteCard(@PathVariable UUID cardId) {
        cardService.delete(cardId);
    }
}
