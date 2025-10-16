package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardCreateRequest;
import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.CardOperationRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.CardStatus;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public AdminController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    // Cards API

    @PostMapping(
            path = "/cards",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public CardData createCard(@RequestBody @Valid CardCreateRequest request) {
        return cardService.create(request.getUserId());
    }

    @GetMapping(path = "/cards", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> getAllCards(@PageableDefault Pageable pageable) {
        return cardService.getAll(pageable);
    }

    @GetMapping("/cards/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> getCardsByUserId(@PathVariable UUID userId, @PageableDefault Pageable pageable) {
        return cardService.getAllByUserId(userId, pageable);
    }

    @PostMapping("/cards/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCard(@RequestBody @Valid CardOperationRequest request) {
        cardService.setStatusByPanLast4(request.getUserId(), request.getPanLast4(), CardStatus.ACTIVE);
    }

    /* NOTE: Not specified in the task, but should be implemented for REST API consistency
    @PostMapping("/cards/{cardId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCard(@PathVariable UUID cardId) {
        return cardService.setStatusById(cardId);
    }
     */

    @PostMapping("/cards/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockCard(@RequestBody @Valid CardOperationRequest request) {
        cardService.setStatusByPanLast4(request.getUserId(), request.getPanLast4(), CardStatus.BLOCKED);
    }

    /* NOTE: Not specified in the task, but should be implemented for REST API consistency
    @PostMapping("/cards/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCard(@PathVariable UUID cardId) {
        return cardService.setStatusById(cardId);
    }
     */

    @DeleteMapping("/cards")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@RequestBody @Valid CardOperationRequest request) {
        cardService.deleteByPanLast4(request.getUserId(), request.getPanLast4());
    }

    // Users API

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserData> getAllUsers(@PageableDefault Pageable pageable) {
        return userService.getAll(pageable);
    }

    @GetMapping("/users/search")
    @ResponseStatus(HttpStatus.OK)
    public UserData getUserByPhoneNumber(
            @RequestParam @Pattern(regexp = RegexPatterns.PHONE_NUMBER) String phoneNumber
    ) {
        return userService.getByPhoneNumber(phoneNumber);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserData getUserById(@PathVariable UUID userId) {
        return userService.getById(userId);
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserById(@PathVariable UUID userId, @RequestBody @Valid UserUpdateRequest request) {
        userService.updateById(userId, request);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable UUID userId) {
        userService.deleteById(userId);
    }
}
