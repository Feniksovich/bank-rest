package com.feniksovich.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feniksovich.bankcards.dto.card.CardData;
import com.feniksovich.bankcards.dto.card.TransactionRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.security.UserPrincipal;
import com.feniksovich.bankcards.testutil.WithMockUserPrincipal;
import com.feniksovich.bankcards.service.card.CardService;
import com.feniksovich.bankcards.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CardService cardService;

    private static final Supplier<UserPrincipal> CONTEXT_PRINCIPAL = () ->
            (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    @Test
    @WithMockUserPrincipal
    void getUser_200() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final String plainDateTime = "2030-01-01T00:00:00Z";

        final UserData userData = UserData.builder()
                .id(userId)
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("9990000000")
                .role(com.feniksovich.bankcards.entity.Role.USER)
                .createdAt(ZonedDateTime.parse(plainDateTime))
                .updatedAt(ZonedDateTime.parse(plainDateTime))
                .build();

        when(userService.getById(userId)).thenReturn(userData);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.phoneNumber").value("9990000000"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.createdAt").value(plainDateTime))
                .andExpect(jsonPath("$.updatedAt").value(plainDateTime));

        verify(userService).getById(userId);
    }

    @Test
    @WithMockUserPrincipal
    void updateUser_204() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .build();

        mockMvc.perform(put("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updateById(eq(userId), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUserPrincipal
    void getCards_200() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final LocalDate expiresAt = LocalDate.of(2030, 5, 1);
        final CardData cardData = CardData.builder()
                .id(UUID.randomUUID())
                .panLast4("1234")
                .expiresAt(expiresAt)
                .cardHolder("IVANOV IVAN")
                .balance(new BigDecimal("100.00"))
                .blocked(false)
                .build();

        final Page<CardData> page = new PageImpl<>(
                Collections.singletonList(cardData),
                PageRequest.of(0, 20), 1
        );

        when(cardService.getAllOwned(eq(userId), any())).thenReturn(page);

        mockMvc.perform(get("/account/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(cardData.getId().toString()))
                .andExpect(jsonPath("$.content[0].panLast4").value("1234"))
                .andExpect(jsonPath("$.content[0].expiresAt").value(expiresAt.format(DateTimeFormatter.ofPattern("MM/yy"))))
                .andExpect(jsonPath("$.content[0].cardHolder").value("IVANOV IVAN"))
                .andExpect(jsonPath("$.content[0].balance").value(100.00))
                .andExpect(jsonPath("$.content[0].blocked").value(false));

        verify(cardService).getAllOwned(eq(userId), any());
    }

    @Test
    @WithMockUserPrincipal
    void getCard_200() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final UUID cardId = UUID.randomUUID();
        final LocalDate expiresAt = LocalDate.of(2030, 12, 1);
        final CardData cardData = CardData.builder()
                .id(cardId)
                .panLast4("1234")
                .expiresAt(expiresAt)
                .cardHolder("IVANOV IVAN")
                .balance(new BigDecimal("100.00"))
                .blocked(false)
                .build();

        when(cardService.getOwnById(userId, cardId)).thenReturn(cardData);

        mockMvc.perform(get("/account/cards/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.panLast4").value("1234"))
                .andExpect(jsonPath("$.expiresAt").value(expiresAt.format(DateTimeFormatter.ofPattern("MM/yy"))))
                .andExpect(jsonPath("$.cardHolder").value("IVANOV IVAN"))
                .andExpect(jsonPath("$.balance").value(100.00))
                .andExpect(jsonPath("$.blocked").value(false));

        verify(cardService).getOwnById(userId, cardId);
    }

    @Test
    @WithMockUserPrincipal
    void blockCard_204() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final UUID cardId = UUID.randomUUID();

        mockMvc.perform(post("/account/cards/{cardId}/block", cardId))
                .andExpect(status().isNoContent());

        verify(cardService).setBlockedOwnById(userId, cardId, true);
    }

    @Test
    @WithMockUserPrincipal
    void performTransaction_204() throws Exception {
        final UUID userId = CONTEXT_PRINCIPAL.get().getId();
        final TransactionRequest request = TransactionRequest.builder()
                .fromPanLast4("1234")
                .toPanLast4("5678")
                .amount(new BigDecimal("10.00"))
                .build();

        mockMvc.perform(post("/account/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(cardService).performTransaction(eq(userId), any(TransactionRequest.class));
    }
}


