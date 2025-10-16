package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.CardStatus;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.service.card.CardService;
import com.feniksovich.bankcards.service.user.UserService;
import com.feniksovich.bankcards.util.RegexPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public UserController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    // User self-management API

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserData getUser(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getById(principal.getId());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        userService.updateById(principal.getId(), request);
    }

    // User cards management API

    @GetMapping(path = "/cards", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> getCards(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault Pageable pageable
    ) {
        return cardService.getAllByUserId(principal.getId(), pageable);
    }

    @GetMapping(path = "/cards/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CardData searchCardByLast4(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @Pattern(regexp = RegexPatterns.CARD_PAN_CHUNK) String panLast4
    ) {
        return cardService.getByPanLast4(principal.getId(), panLast4);
    }

    @GetMapping(path = "/cards/{cardId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CardData getCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        return cardService.getById(principal.getId(), cardId);
    }

    @PostMapping(path = "/cards/{cardId}/block", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID cardId
    ) {
        cardService.setStatusById(principal.getId(), cardId, CardStatus.BLOCKED);
    }

    @PostMapping(
            path = "/transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void performTransaction(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid TransactionRequest request
    ) {
        cardService.performTransaction(principal.getId(), request);
    }

}
