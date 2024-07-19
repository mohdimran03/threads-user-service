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
import java.util.UUID;

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
        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
