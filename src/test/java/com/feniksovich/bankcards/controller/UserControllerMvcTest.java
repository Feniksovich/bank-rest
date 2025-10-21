package com.feniksovich.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.Role;
import com.feniksovich.bankcards.service.user.UserService;
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

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getAllUsers_200() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UserData userData = UserData.builder()
                .id(userId)
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("9990000000")
                .role(Role.USER)
                .build();

        final Page<UserData> page = new PageImpl<>(
                Collections.singletonList(userData),
                PageRequest.of(0, 20), 1
        );

        when(userService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].lastName").value("Иванов"))
                .andExpect(jsonPath("$.content[0].firstName").value("Иван"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("9990000000"))
                .andExpect(jsonPath("$.content[0].role").value(Role.USER.name()));

        verify(userService).getAll(any());
    }

    @Test
    void searchUserByPhoneNumber_200() throws Exception {
        final UUID userId = UUID.randomUUID();
        final String phoneNumber = "9990000000";
        final UserData userData = UserData.builder()
                .id(userId)
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();

        when(userService.getByPhoneNumber(phoneNumber)).thenReturn(userData);

        mockMvc.perform(get("/users/search").param("phoneNumber", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));

        verify(userService).getByPhoneNumber(phoneNumber);
    }

    @Test
    void getUser_200() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UserData userData = UserData.builder()
                .id(userId)
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("9990000000")
                .role(Role.USER)
                .build();

        when(userService.getById(userId)).thenReturn(userData);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.phoneNumber").value("9990000000"))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));

        verify(userService).getById(userId);
    }

    @Test
    void updateUser_204() throws Exception {
        final UUID userId = UUID.randomUUID();
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .build();

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updateById(eq(userId), argThat(req ->
                "Иванов".equals(req.getLastName()) && "Иван".equals(req.getFirstName())
        ));
    }

    @Test
    void deleteUser_204() throws Exception {
        final UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteById(userId);
    }
}