package com.feniksovich.bankcards.service.card;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.entity.Card;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.exception.CardOperationException;
import com.feniksovich.bankcards.exception.ResourceNotFoundException;
import com.feniksovich.bankcards.repository.CardRepository;
import com.feniksovich.bankcards.repository.UserRepository;
import com.feniksovich.bankcards.security.crypto.CryptoService;
import com.feniksovich.bankcards.util.CardUtil;
import com.feniksovich.bankcards.util.TransliterationUtil;
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
    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final ModelMapper modelMapper;

    private static final Supplier<ResourceNotFoundException> NOT_FOUND_EXCEPTION =
            () -> new ResourceNotFoundException("Card not found");

    @Autowired
    public CardServiceImpl(
            CardRepository cardRepository,
            UserRepository userRepository,
            CryptoService cryptoService,
            ModelMapper modelMapper
    ) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.modelMapper = modelMapper;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CardData getById(UUID cardId) {
        return cardRepository.findById(cardId)
                .map(card -> modelMapper.map(card, CardData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CardData create(UUID userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        final String pan = generateUniquePanSafely(userId, 5);
        final String panLast4 = pan.substring(pan.length() - 4);
        final String encryptedPan = cryptoService.encrypt(pan);
        final String cardHolder = TransliterationUtil.transliterate(
                String.format("%s %s", user.getLastName(), user.getFirstName())
        ).toUpperCase();
        final LocalDate expiresAt = LocalDate.now().withDayOfMonth(1).plusYears(5);

        final Card card = Card.builder()
                .user(user)
                .panEncrypted(encryptedPan)
                .panLast4(panLast4)
                .cardHolder(cardHolder)
                .expiresAt(expiresAt)
                .balance(BigDecimal.ZERO)
                .build();

        return modelMapper.map(cardRepository.save(card), CardData.class);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID cardId) {
        final Card card = cardRepository.findById(cardId).orElseThrow(NOT_FOUND_EXCEPTION);

        if (card.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new CardOperationException("Cannot delete card with non-zero balance");
        }

        cardRepository.deleteById(cardId);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Page<CardData> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(card -> modelMapper.map(card, CardData.class));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void setBlockedById(UUID cardId, boolean blocked) {
        final Card card = cardRepository.findById(cardId).orElseThrow(NOT_FOUND_EXCEPTION);
        setBlocked(card, blocked);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CardData getOwnById(UUID userId, UUID cardId) {
        return cardRepository.findByUserIdAndId(userId, cardId)
                .map(card -> modelMapper.map(card, CardData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Page<CardData> getAllOwned(UUID userId, Pageable pageable) {
        return cardRepository.findAllByUserId(userId, pageable)
                .map(card -> modelMapper.map(card, CardData.class));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void setBlockedOwnById(UUID userId, UUID cardId, boolean blocked) {
        final Card card = cardRepository.findByUserIdAndId(userId, cardId).orElseThrow(NOT_FOUND_EXCEPTION);
        setBlocked(card, blocked);
    }

    /** {@inheritDoc} */
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

    /**
     * Генерирует уникальный PAN карты с ограничением количества попыток,
     * проверяя уникальность последних 4 цифр для пользователя.
     *
     * @param userId      идентификатор пользователя
     * @param maxAttempts максимальное число попыток
     * @return сгенерированный PAN
     */
    private String generateUniquePanSafely(UUID userId, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            final String pan = CardUtil.generateCardPan();
            final String panLast4 = pan.substring(pan.length() - 4);

            if (cardRepository.existsByUserIdAndPanLast4(userId, panLast4)) {
                continue;
            }

            return pan;
        }

        throw new IllegalStateException("Failed to generate unique card PAN after maximum attempts");
    }

    /**
     * Находит карту пользователя по последним 4 цифрам PAN или бросает исключение.
     *
     * @param userId  идентификатор пользователя
     * @param panLast4 последние 4 цифры PAN
     * @return найденная карта
     */
    private Card findByUserIdAndPanLast4OrThrow(UUID userId, String panLast4) {
        return cardRepository.findByUserIdAndPanLast4(userId, panLast4)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with last 4 pan specified"));
    }

    /**
     * Устанавливает состояние блокировки для карты, избегая лишних сохранений.
     *
     * @param card  карта
     * @param state целевое состояние блокировки
     */
    private void setBlocked(Card card, boolean state) {
        if (card.isBlocked() == state) {
            return;
        }
        card.setBlocked(state);
        cardRepository.save(card);
    }

    /**
     * Валидирует параметры перевода между картами.
     *
     * @param fromCard карта-источник
     * @param toCard   карта-назначение
     * @param amount   сумма перевода
     */
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
