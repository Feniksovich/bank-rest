package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.entity.Card;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.exception.CardOperationException;
import com.feniksovich.bankcards.exception.ResourceNotFoundException;
import com.feniksovich.bankcards.repository.CardRepository;
import com.feniksovich.bankcards.repository.UserRepository;
import com.feniksovich.bankcards.security.crypto.CryptoService;
import com.feniksovich.bankcards.service.card.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User existingUser;
    private Card existingCard;
    private CardData existingCardData;
    private UUID userId;
    private UUID cardId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        cardId = UUID.randomUUID();

        existingUser = User.builder()
                .id(userId)
                .firstName("Иван")
                .lastName("Иванов")
                .phoneNumber("0987654321")
                .build();

        existingCard = Card.builder()
                .id(cardId)
                .user(existingUser)
                .panEncrypted("encryptedPan")
                .panLast4("1234")
                .cardHolder("IVANOV IVAN")
                .expiresAt(LocalDate.now().plusYears(5).withDayOfMonth(1))
                .balance(BigDecimal.valueOf(1000))
                .blocked(false)
                .build();

        existingCardData = CardData.builder()
                .id(cardId)
                .panLast4("1234")
                .cardHolder("IVANOV IVAN")
                .expiresAt(existingCard.getExpiresAt())
                .balance(BigDecimal.valueOf(1000))
                .blocked(false)
                .build();
    }

    @Test
    void getById_WhenCardExists_ShouldReturnCardData() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(modelMapper.map(existingCard, CardData.class)).thenReturn(existingCardData);

        final CardData result = cardService.getById(cardId);

        assertThat(result).isEqualTo(existingCardData);
        verify(cardRepository).findById(cardId);
        verify(modelMapper).map(existingCard, CardData.class);
    }

    @Test
    void getById_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getById(cardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(cardRepository).findById(cardId);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void create_WhenValidUserId_ShouldReturnCardData() {
        final String generatedPan = "1234567890123456";
        final String encryptedPan = "encryptedPan";
        final Card savedCard = Card.builder()
                .id(cardId)
                .user(existingUser)
                .panEncrypted(encryptedPan)
                .panLast4("3456")
                .cardHolder("IVANOV IVAN")
                .expiresAt(LocalDate.now().plusYears(5).withDayOfMonth(1))
                .balance(BigDecimal.ZERO)
                .blocked(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(cardRepository.existsByUserIdAndPanLast4(userId, "3456")).thenReturn(false);
        when(cryptoService.encrypt(generatedPan)).thenReturn(encryptedPan);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(modelMapper.map(savedCard, CardData.class)).thenReturn(existingCardData);

        final CardData result = cardService.create(userId);

        assertThat(result).isEqualTo(existingCardData);
        verify(userRepository).findById(userId);
        verify(cryptoService).encrypt(anyString());
        verify(cardRepository).save(any(Card.class));
        verify(modelMapper).map(savedCard, CardData.class);
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.create(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(cardRepository, cryptoService, modelMapper);
    }

    @Test
    void delete_WhenCardExistsAndZeroBalance_ShouldDeleteCard() {
        existingCard.setBalance(BigDecimal.ZERO);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        doNothing().when(cardRepository).deleteById(cardId);

        cardService.delete(cardId);

        verify(cardRepository).findById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void delete_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.delete(cardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(cardRepository).findById(cardId);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void delete_WhenCardHasNonZeroBalance_ShouldThrowCardOperationException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));

        assertThatThrownBy(() -> cardService.delete(cardId))
                .isInstanceOf(CardOperationException.class)
                .hasMessageContaining("non-zero balance");

        verify(cardRepository).findById(cardId);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getAll_WhenCardsExist_ShouldReturnPageOfCardData() {
        final Pageable pageable = PageRequest.of(0, 10);
        final List<Card> cards = List.of(existingCard);
        final Page<Card> cardPage = new PageImpl<>(cards, pageable, 1);

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(modelMapper.map(existingCard, CardData.class)).thenReturn(existingCardData);

        final Page<CardData> result = cardService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(existingCardData);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAll(pageable);
        verify(modelMapper).map(existingCard, CardData.class);
    }

    @Test
    void setBlockedById_WhenCardExists_ShouldUpdateBlockedStatus() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(existingCard)).thenReturn(existingCard);

        cardService.setBlockedById(cardId, true);

        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(existingCard);
    }

    @Test
    void setBlockedById_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.setBlockedById(cardId, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(cardRepository).findById(cardId);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getOwnById_WhenCardExists_ShouldReturnCardData() {
        when(cardRepository.findByUserIdAndId(userId, cardId)).thenReturn(Optional.of(existingCard));
        when(modelMapper.map(existingCard, CardData.class)).thenReturn(existingCardData);

        final CardData result = cardService.getOwnById(userId, cardId);

        assertThat(result).isEqualTo(existingCardData);
        verify(cardRepository).findByUserIdAndId(userId, cardId);
        verify(modelMapper).map(existingCard, CardData.class);
    }

    @Test
    void getOwnById_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findByUserIdAndId(userId, cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getOwnById(userId, cardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(cardRepository).findByUserIdAndId(userId, cardId);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void getAllOwned_WhenCardsExist_ShouldReturnPageOfCardData() {
        final Pageable pageable = PageRequest.of(0, 10);
        final List<Card> cards = List.of(existingCard);
        final Page<Card> cardPage = new PageImpl<>(cards, pageable, 1);

        when(cardRepository.findAllByUserId(userId, pageable)).thenReturn(cardPage);
        when(modelMapper.map(existingCard, CardData.class)).thenReturn(existingCardData);

        final Page<CardData> result = cardService.getAllOwned(userId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(existingCardData);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(cardRepository).findAllByUserId(userId, pageable);
        verify(modelMapper).map(existingCard, CardData.class);
    }

    @Test
    void setBlockedOwnById_WhenCardExists_ShouldUpdateBlockedStatus() {
        when(cardRepository.findByUserIdAndId(userId, cardId)).thenReturn(Optional.of(existingCard));
        when(cardRepository.save(existingCard)).thenReturn(existingCard);

        cardService.setBlockedOwnById(userId, cardId, true);

        verify(cardRepository).findByUserIdAndId(userId, cardId);
        verify(cardRepository).save(existingCard);
    }

    @Test
    void setBlockedOwnById_WhenCardNotFound_ShouldThrowResourceNotFoundException() {
        when(cardRepository.findByUserIdAndId(userId, cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.setBlockedOwnById(userId, cardId, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(cardRepository).findByUserIdAndId(userId, cardId);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void performTransaction_WhenValidRequest_ShouldPerformTransaction() {
        final Card fromCard = Card.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .panLast4("1111")
                .balance(BigDecimal.valueOf(500))
                .blocked(false)
                .expiresAt(LocalDate.now().plusYears(1))
                .build();

        final Card toCard = Card.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .panLast4("2222")
                .balance(BigDecimal.valueOf(200))
                .blocked(false)
                .expiresAt(LocalDate.now().plusYears(1))
                .build();

        final TransactionRequest request = TransactionRequest.builder()
                .fromPanLast4("1111")
                .toPanLast4("2222")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(cardRepository.findByUserIdAndPanLast4(userId, "1111")).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByUserIdAndPanLast4(userId, "2222")).thenReturn(Optional.of(toCard));
        when(cardRepository.saveAll(anyList())).thenReturn(List.of(fromCard, toCard));

        cardService.performTransaction(userId, request);

        verify(cardRepository).findByUserIdAndPanLast4(userId, "1111");
        verify(cardRepository).findByUserIdAndPanLast4(userId, "2222");
        verify(cardRepository).saveAll(anyList());
    }

    @Test
    void performTransaction_WhenSameCard_ShouldThrowCardOperationException() {
        final TransactionRequest request = TransactionRequest.builder()
                .fromPanLast4("1111")
                .toPanLast4("1111")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(cardRepository.findByUserIdAndPanLast4(userId, "1111")).thenReturn(Optional.of(existingCard));

        assertThatThrownBy(() -> cardService.performTransaction(userId, request))
                .isInstanceOf(CardOperationException.class)
                .hasMessageContaining("same card");

        verify(cardRepository).findByUserIdAndPanLast4(userId, "1111");
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void performTransaction_WhenCardBlocked_ShouldThrowCardOperationException() {
        final Card blockedCard = Card.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .panLast4("1111")
                .balance(BigDecimal.valueOf(500))
                .blocked(true)
                .expiresAt(LocalDate.now().plusYears(1))
                .build();

        final TransactionRequest request = TransactionRequest.builder()
                .fromPanLast4("1111")
                .toPanLast4("2222")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(cardRepository.findByUserIdAndPanLast4(userId, "1111")).thenReturn(Optional.of(blockedCard));
        when(cardRepository.findByUserIdAndPanLast4(userId, "2222")).thenReturn(Optional.of(existingCard));

        assertThatThrownBy(() -> cardService.performTransaction(userId, request))
                .isInstanceOf(CardOperationException.class)
                .hasMessageContaining("blocked");

        verify(cardRepository).findByUserIdAndPanLast4(userId, "1111");
        verify(cardRepository).findByUserIdAndPanLast4(userId, "2222");
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void performTransaction_WhenInsufficientFunds_ShouldThrowCardOperationException() {
        final Card lowBalanceCard = Card.builder()
                .id(UUID.randomUUID())
                .user(existingUser)
                .panLast4("1111")
                .balance(BigDecimal.valueOf(50))
                .blocked(false)
                .expiresAt(LocalDate.now().plusYears(1))
                .build();

        final TransactionRequest request = TransactionRequest.builder()
                .fromPanLast4("1111")
                .toPanLast4("2222")
                .amount(BigDecimal.valueOf(100))
                .build();

        when(cardRepository.findByUserIdAndPanLast4(userId, "1111")).thenReturn(Optional.of(lowBalanceCard));
        when(cardRepository.findByUserIdAndPanLast4(userId, "2222")).thenReturn(Optional.of(existingCard));

        assertThatThrownBy(() -> cardService.performTransaction(userId, request))
                .isInstanceOf(CardOperationException.class)
                .hasMessageContaining("not enough funds");

        verify(cardRepository).findByUserIdAndPanLast4(userId, "1111");
        verify(cardRepository).findByUserIdAndPanLast4(userId, "2222");
        verifyNoMoreInteractions(cardRepository);
    }
}
