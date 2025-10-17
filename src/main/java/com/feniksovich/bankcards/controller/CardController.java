package com.feniksovich.bankcards.controller;

import com.feniksovich.bankcards.dto.card.CardCreateRequest;
import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.service.card.CardService;
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
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> getAllCards(@PageableDefault Pageable pageable) {
        return cardService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardData> searchCardsByUserId(@RequestParam UUID userId, @PageableDefault Pageable pageable) {
        return cardService.getAllOwned(userId, pageable);
    }

    @GetMapping("/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardData getCard(@PathVariable UUID cardId) {
        return cardService.getById(cardId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardData createCard(@RequestBody @Valid CardCreateRequest request) {
        return cardService.create(request.getUserId());
    }

    @PostMapping("/{cardId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateCard(@PathVariable UUID cardId) {
        cardService.setBlockedById(cardId, false);
    }

    @PostMapping("/{cardId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockCard(@PathVariable UUID cardId) {
        cardService.setBlockedById(cardId, true);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable UUID cardId) {
        cardService.delete(cardId);
    }
}
