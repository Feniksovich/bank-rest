package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.Role;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.exception.ResourceConflictException;
import com.feniksovich.bankcards.exception.ResourceNotFoundException;
import com.feniksovich.bankcards.repository.UserRepository;
import com.feniksovich.bankcards.service.user.UserServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private UserData existingUserData;

    @BeforeEach
    void setup() {
        existingUser = User.builder()
                .id(UUID.randomUUID())
                .phoneNumber("987654321")
                .firstName("Иван")
                .lastName("Иванов")
                .role(Role.USER)
                .build();

        existingUserData = UserData.builder()
                .id(existingUser.getId())
                .phoneNumber(existingUser.getPhoneNumber())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .role(existingUser.getRole())
                .build();
    }

    @Test
    void register_WhenValidRequest_ShouldReturnUser() {
        final String rawPassword = "plainPassword";
        final String encodedPassword = "encodedPassword";

        final SignUpRequest request = SignUpRequest.builder()
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("1234567890")
                .password(rawPassword)
                .build();

        final User user = User.builder()
                .id(UUID.randomUUID())
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("1234567890")
                .password(encodedPassword)
                .build();

        final UserData userData = UserData.builder()
                .id(user.getId())
                .lastName("Иванов")
                .firstName("Иван")
                .phoneNumber("1234567890")
                .build();

        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserData.class)).thenReturn(userData);

        final UserData result = userService.register(request);

        assertThat(result).isEqualTo(userData);
        verify(userRepository).existsByPhoneNumber(request.getPhoneNumber());
        verify(passwordEncoder).encode(rawPassword);
        verify(modelMapper).map(request, User.class);
        verify(userRepository).save(user);
        verify(modelMapper).map(user, UserData.class);
    }

    @Test
    void register_WhenPhoneNumberExists_ShouldThrowResourceConflictException() {
        final SignUpRequest request = SignUpRequest.builder()
                .phoneNumber("1234567890")
                .build();

        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).existsByPhoneNumber(request.getPhoneNumber());
        verifyNoMoreInteractions(userRepository, modelMapper);
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUserData() {
        final UUID userId = existingUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(existingUser, UserData.class)).thenReturn(existingUserData);

        final UserData result = userService.getById(userId);

        assertThat(result).isEqualTo(existingUserData);
        verify(userRepository).findById(userId);
        verify(modelMapper).map(existingUser, UserData.class);
    }

    @Test
    void getById_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        final UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void getByPhoneNumber_WhenUserExists_ShouldReturnUserData() {
        final String phoneNumber = existingUser.getPhoneNumber();

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(existingUser));
        when(modelMapper.map(existingUser, UserData.class)).thenReturn(existingUserData);

        final UserData result = userService.getByPhoneNumber(phoneNumber);

        assertThat(result).isEqualTo(existingUserData);
        verify(userRepository).findByPhoneNumber(phoneNumber);
        verify(modelMapper).map(existingUser, UserData.class);
    }

    @Test
    void getByPhoneNumber_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        final String phoneNumber = "999999999";

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByPhoneNumber(phoneNumber))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findByPhoneNumber(phoneNumber);
        verifyNoMoreInteractions(modelMapper);
    }

    @Test
    void getAll_WhenUsersExist_ShouldReturnPageOfUserData() {
        final Pageable pageable = PageRequest.of(0, 10);
        final List<User> users = List.of(existingUser);
        final Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(modelMapper.map(existingUser, UserData.class)).thenReturn(existingUserData);

        final Page<UserData> result = userService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(existingUserData);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(userRepository).findAll(pageable);
        verify(modelMapper).map(existingUser, UserData.class);
    }

    @Test
    void updateById_WhenUserExists_ShouldUpdateUser() {
        final UUID userId = existingUser.getId();
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .firstName("Петр")
                .lastName("Петров")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.updateById(userId, request);

        verify(userRepository).findById(userId);
        verify(modelMapper).map(request, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateById_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        final UUID userId = UUID.randomUUID();
        final UserUpdateRequest request = UserUpdateRequest.builder()
                .firstName("Петр")
                .lastName("Петров")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateById(userId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(modelMapper, userRepository);
    }

    @Test
    void deleteById_WhenUserExists_ShouldDeleteUser() {
        final UUID userId = existingUser.getId();
        doNothing().when(userRepository).deleteById(userId);
        userService.deleteById(userId);
        verify(userRepository).deleteById(userId);
    }
}
