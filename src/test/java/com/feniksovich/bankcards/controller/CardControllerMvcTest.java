package com.feniksovich.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feniksovich.bankcards.dto.card.CardCreateRequest;
import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.service.card.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;

    @Test
    void getAllCards_200() throws Exception {
        final UUID cardId = UUID.randomUUID();
        final LocalDate expiresAt = LocalDate.of(2030, 1, 1);
        final CardData card = CardData.builder()
                .id(cardId)
                .panLast4("1234")
                .expiresAt(expiresAt)
                .cardHolder("IVANOV IVAN")
                .balance(new BigDecimal("100.00"))
                .blocked(false)
                .build();

        final Page<CardData> page = new PageImpl<>(
                Collections.singletonList(card),
                PageRequest.of(0, 20), 1
        );

        when(cardService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardId.toString()))
                .andExpect(jsonPath("$.content[0].panLast4").value("1234"))
                .andExpect(jsonPath("$.content[0].expiresAt").value("01/30"))
                .andExpect(jsonPath("$.content[0].cardHolder").value("IVANOV IVAN"))
                .andExpect(jsonPath("$.content[0].balance").value(100.00))
                .andExpect(jsonPath("$.content[0].blocked").value(false));

        verify(cardService).getAll(any());
    }

    @Test
    void searchCardsByUserId_200() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UUID cardId = UUID.randomUUID();
        final LocalDate expiresAt = LocalDate.of(2031, 6, 1);
        final CardData card = CardData.builder()
                .id(cardId)
                .panLast4("5678")
                .expiresAt(expiresAt)
                .cardHolder("PETROV PETR")
                .balance(new BigDecimal("250.50"))
                .blocked(true)
                .build();

        final Page<CardData> page = new PageImpl<>(
                Collections.singletonList(card),
                PageRequest.of(0, 20), 1
        );

        when(cardService.getAllOwned(eq(userId), any())).thenReturn(page);

        mockMvc.perform(get("/cards/search").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardId.toString()))
                .andExpect(jsonPath("$.content[0].panLast4").value("5678"))
                .andExpect(jsonPath("$.content[0].expiresAt").value("06/31"))
                .andExpect(jsonPath("$.content[0].cardHolder").value("PETROV PETR"))
                .andExpect(jsonPath("$.content[0].balance").value(250.50))
                .andExpect(jsonPath("$.content[0].blocked").value(true));

        verify(cardService).getAllOwned(eq(userId), any());
    }

    @Test
    void getCard_200() throws Exception {
        final UUID cardId = UUID.randomUUID();
        final LocalDate expiresAt = LocalDate.of(2028, 12, 1);
        final CardData card = CardData.builder()
                .id(cardId)
                .panLast4("9999")
                .expiresAt(expiresAt)
                .cardHolder("IVANOV IVAN")
                .balance(new BigDecimal("77.77"))
                .blocked(true)
                .build();

        when(cardService.getById(cardId)).thenReturn(card);

        mockMvc.perform(get("/cards/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.panLast4").value("9999"))
                .andExpect(jsonPath("$.expiresAt").value("12/28"))
                .andExpect(jsonPath("$.cardHolder").value("IVANOV IVAN"))
                .andExpect(jsonPath("$.balance").value(77.77))
                .andExpect(jsonPath("$.blocked").value(true));

        verify(cardService).getById(cardId);
    }

    @Test
    void createCard_201() throws Exception {
        final UUID userId = UUID.randomUUID();
        final CardCreateRequest request = new CardCreateRequest(userId);
        final UUID cardId = UUID.randomUUID();
        final LocalDate expiresAt = LocalDate.of(2032, 3, 1);
        final CardData created = CardData.builder()
                .id(cardId)
                .panLast4("4321")
                .expiresAt(expiresAt)
                .cardHolder("DOE JOHN")
                .balance(new BigDecimal("0.00"))
                .blocked(false)
                .build();

        when(cardService.create(userId)).thenReturn(created);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.panLast4").value("4321"))
                .andExpect(jsonPath("$.expiresAt").value("03/32"))
                .andExpect(jsonPath("$.cardHolder").value("DOE JOHN"))
                .andExpect(jsonPath("$.balance").value(0.00))
                .andExpect(jsonPath("$.blocked").value(false));

        verify(cardService).create(userId);
    }

    @Test
    void activateCard_204() throws Exception {
        final UUID cardId = UUID.randomUUID();

        mockMvc.perform(post("/cards/{cardId}/activate", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).setBlockedById(cardId, false);
    }

    @Test
    void blockCard_204() throws Exception {
        final UUID cardId = UUID.randomUUID();

        mockMvc.perform(post("/cards/{cardId}/block", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).setBlockedById(cardId, true);
    }

    @Test
    void deleteCard_204() throws Exception {
        final UUID cardId = UUID.randomUUID();

        mockMvc.perform(delete("/cards/{cardId}", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).delete(cardId);
    }
}


