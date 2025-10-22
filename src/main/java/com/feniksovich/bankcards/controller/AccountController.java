package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.service.card.CardService;
import com.feniksovich.bankcards.service.user.UserService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/account")
@Tag(name = "User Account", description = "API for managing user profile and cards")
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public AccountController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    // User profile management API

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user profile", description = "Returns data of the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public UserData getUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getById(principal.getId());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update user profile", description = "Updates data of the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User profile successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public void updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        userService.updateById(principal.getId(), request);
    }

    // User cards management API

    @GetMapping("/cards")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user cards", description = "Returns list of all cards belonging to the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards list successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    public Page<CardData> getCards(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault Pageable pageable
    ) {
        return cardService.getAllOwned(principal.getId(), pageable);
    }

    @GetMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get card by ID", description = "Returns data of a specific user card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card data successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public CardData getCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        return cardService.getOwnById(principal.getId(), cardId);
    }

    @PostMapping("/cards/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Block card", description = "Blocks user card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card successfully blocked"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    public void blockCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        cardService.setBlockedOwnById(principal.getId(), cardId, true);
    }

    @PostMapping(path = "/transaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Perform transaction", description = "Executes transaction between user cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction successfully executed"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "422", description = "Insufficient funds or card blocked")
    })
    public void performTransaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid TransactionRequest request
    ) {
        cardService.performTransaction(principal.getId(), request);
    }

}
