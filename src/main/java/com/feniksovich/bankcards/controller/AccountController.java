package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.service.card.CardService;
import com.feniksovich.bankcards.service.user.UserService;
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
    public UserData getUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getById(principal.getId());
    }

    @PutMapping
    public void updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        userService.updateById(principal.getId(), request);
    }

    // User cards management API

    @GetMapping("/cards")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> getCards(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault Pageable pageable
    ) {
        return cardService.getAllOwned(principal.getId(), pageable);
    }

    @GetMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardData getCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        return cardService.getOwnById(principal.getId(), cardId);
    }

    @PostMapping("/cards/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        cardService.setBlockedOwnById(principal.getId(), cardId, true);
    }

    @PostMapping(path = "/transaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void performTransaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid TransactionRequest request
    ) {
        cardService.performTransaction(principal.getId(), request);
    }

}
