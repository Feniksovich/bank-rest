package com.feniksovich.bankcards.security;

import com.feniksovich.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExtendedUserDetailsServiceImpl implements ExtendedUserDetailsService {

    private final UserRepository repository;

    @Autowired
    public ExtendedUserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public UserPrincipal loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return repository.findByPhoneNumber(phoneNumber)
                .map(UserPrincipal::of)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /** {@inheritDoc} */
    @Override
    public UserDetails loadUserById(UUID id) throws UsernameNotFoundException {
        return repository.findById(id)
                .map(UserPrincipal::of)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
