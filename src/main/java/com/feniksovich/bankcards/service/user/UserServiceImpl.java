package com.feniksovich.bankcards.service.user;

import com.feniksovich.bankcards.dto.auth.SignUpRequest;
import com.feniksovich.bankcards.dto.user.UserData;
import com.feniksovich.bankcards.dto.user.UserUpdateRequest;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.exception.ResourceConflictException;
import com.feniksovich.bankcards.exception.ResourceNotFoundException;
import com.feniksovich.bankcards.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Supplier;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private static final Supplier<ResourceNotFoundException> NOT_FOUND_EXCEPTION =
            () -> new ResourceNotFoundException("User not found");

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    /** {@inheritDoc} */
    @Transactional
    public UserData register(SignUpRequest request) {
        if (repository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResourceConflictException("User with specified phone number already exists");
        }

        // Hash the password before saving
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        final User user = modelMapper.map(request, User.class);
        return modelMapper.map(repository.save(user), UserData.class);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UserData getById(UUID id) {
        return repository.findById(id)
                .map(user -> modelMapper.map(user, UserData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UserData getByPhoneNumber(String phoneNumber) {
        return repository.findByPhoneNumber(phoneNumber)
                .map(user -> modelMapper.map(user, UserData.class))
                .orElseThrow(NOT_FOUND_EXCEPTION);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Page<UserData> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserData.class));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void updateById(UUID id, UserUpdateRequest request) {
        final User user = repository.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);
        modelMapper.map(request, user);
        repository.save(user);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
