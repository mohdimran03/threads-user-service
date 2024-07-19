package com.example.userservice.security.services;

import com.example.userservice.models.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.userservice.security.services.UserDetailsImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String uid) {
        UUID userId;
        try {
            userId = UUID.fromString(uid);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid UUID format: " + uid);
        }

        Optional<User> userOpt = repository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        User user = userOpt.get();

        Set<SimpleGrantedAuthority> authorities = Collections.emptySet();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            authorities = user.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
