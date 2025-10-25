package com.feniksovich.bankcards.service.auth;

import com.feniksovich.bankcards.entity.UserRefreshToken;
import com.feniksovich.bankcards.repository.UserRefreshTokenRepository;
import com.feniksovich.bankcards.security.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserRefreshTokenServiceImpl implements UserRefreshTokenService {

    private final UserRefreshTokenRepository repository;

    @Autowired
    public UserRefreshTokenServiceImpl(UserRefreshTokenRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void track(JwtToken jwtToken) {
        final UserRefreshToken userRefreshToken = new UserRefreshToken(
                jwtToken.userId(),
                jwtToken.id(),
                jwtToken.expiresAt()
        );
        repository.save(userRefreshToken);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public boolean isTracked(JwtToken jwtToken) {
        return repository.existsById(jwtToken.id());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void invalidate(JwtToken jwtToken) {
        repository.deleteById(jwtToken.id());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void invalidateAll(UUID userId) {
        repository.deleteAllByUserId(userId);
    }
}
