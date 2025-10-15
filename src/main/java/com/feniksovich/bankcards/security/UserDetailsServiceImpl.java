package com.feniksovich.bankcards.security;

import com.feniksovich.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserPrincipal loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return repository.findByPhoneNumber(phoneNumber)
                .map(UserPrincipal::of)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
