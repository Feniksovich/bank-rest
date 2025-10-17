package com.feniksovich.bankcards.service.card;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.entity.Card;
import com.feniksovich.bankcards.exception.CardOperationException;
import com.feniksovich.bankcards.exception.ResourceNotFoundException;
import com.feniksovich.bankcards.repository.CardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;

    private static final Supplier<ResourceNotFoundException> NOT_FOUND_EXCEPTION =
            () -> new ResourceNotFoundException("Card not found");

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CardData getById(UUID cardId) {
        return cardRepository.findById(cardId)
                .map(card -> modelMapper.map(card, CardData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    @Override
    @Transactional
    public CardData create(UUID userId) {
        //TODO
        return null;
    }

    @Override
    @Transactional
    public void delete(UUID cardId) {
        final Card card = cardRepository.findById(cardId).orElseThrow(NOT_FOUND_EXCEPTION);

        if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new CardOperationException("Cannot delete card with non-zero balance");
        }

        cardRepository.deleteById(cardId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardData> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(card -> modelMapper.map(card, CardData.class));
    }

    @Override
    @Transactional
    public void setBlockedById(UUID cardId, boolean blocked) {
        final Card card = cardRepository.findById(cardId).orElseThrow(NOT_FOUND_EXCEPTION);
        setBlocked(card, blocked);
    }

    @Override
    @Transactional(readOnly = true)
    public CardData getOwnById(UUID userId, UUID cardId) {
        return cardRepository.findByUserIdAndId(userId, cardId)
                .map(card -> modelMapper.map(card, CardData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardData> getAllOwned(UUID userId, Pageable pageable) {
        return cardRepository.findAllByUserId(userId, pageable)
                .map(card -> modelMapper.map(card, CardData.class));
    }

    @Override
    @Transactional
    public void setBlockedOwnById(UUID userId, UUID cardId, boolean blocked) {
        final Card card = cardRepository.findByUserIdAndId(userId, cardId).orElseThrow(NOT_FOUND_EXCEPTION);
        setBlocked(card, blocked);
    }

    @Override
    @Transactional
    public void performTransaction(UUID userId, TransactionRequest request) {
        final Card fromCard = findByUserIdAndPanLast4OrThrow(userId, request.getFromPanLast4());
        final Card toCard = findByUserIdAndPanLast4OrThrow(userId, request.getToPanLast4());
        final BigDecimal amount = request.getAmount();

        validateTransaction(fromCard, toCard, amount);

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.saveAll(List.of(fromCard, toCard));
    }

    private Card findByUserIdAndPanLast4OrThrow(UUID userId, String panLast4) {
        return cardRepository.findByUserIdAndPanLast4(userId, panLast4)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with last 4 pan specified"));
    }

    private void setBlocked(Card card, boolean state) {
        if (card.isBlocked() == state) {
            return;
        }
        card.setBlocked(state);
        cardRepository.save(card);
    }

    private static void validateTransaction(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getId().equals(toCard.getId())) {
            throw new CardOperationException("Cannot transfer to the same card");
        }

        if (fromCard.isBlocked() || toCard.isBlocked()) {
            throw new CardOperationException("One of the cards is blocked");
        }

        final LocalDate now = LocalDate.now();
        if (toCard.getExpiresAt().isBefore(now) || fromCard.getExpiresAt().isBefore(now)) {
            throw new CardOperationException("One of the cards is expired");
        }

        if (fromCard.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new CardOperationException("The card is not enough funds");
        }
    }
}
