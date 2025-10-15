package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.user.SignUpRequest;
import com.feniksovich.bankcards.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(SignUpRequest request);
    User retrieveUserById(UUID id);
}
