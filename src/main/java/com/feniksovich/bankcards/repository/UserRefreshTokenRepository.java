package com.feniksovich.bankcards.repository;

import com.feniksovich.bankcards.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, UUID> {
    void deleteAllByUserId(UUID userId);
}
