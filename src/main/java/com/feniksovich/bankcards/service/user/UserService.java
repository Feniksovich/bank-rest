package com.feniksovich.bankcards.service.user;

import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    User register(SignUpRequest request);
    UserData getById(UUID id);
    UserData getByPhoneNumber(String phoneNumber);
    Page<UserData> getAll(Pageable pageable);
    void updateById(UUID id, UserUpdateRequest request);
    void deleteById(UUID id);
}
