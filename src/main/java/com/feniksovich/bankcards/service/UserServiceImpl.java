package com.feniksovich.bankcards.service;

import com.feniksovich.bankcards.dto.user.SignUpRequest;
import com.feniksovich.bankcards.entity.User;
import com.feniksovich.bankcards.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public User createUser(SignUpRequest request) {
        if (repository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with specified phone number already exists");
        }

        // Hash the password before saving
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        final User user = modelMapper.map(request, User.class);
        return repository.save(user);
    }

    @Override
    public User retrieveUserById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

}
