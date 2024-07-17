package com.example.userservice.security.services;

import com.example.userservice.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.example.userservice.security.services.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userId) {

        //todo fetch user from database

        User user = new User(
                "username",
                "password",
                24
        );
        return UserDetailsImpl.build(user);
    }
}
