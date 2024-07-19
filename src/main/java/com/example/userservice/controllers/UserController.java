package com.example.userservice.controllers;

import com.example.userservice.services.UserDto;
import com.example.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1")
@EnableMethodSecurity
public class UserController {

    @Autowired
    UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserDto userDto) {
        return userService.signUpUser(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return userService.loginUser(userDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto, @PathVariable UUID userId) {
        return userService.updateUser(userDto, userId);
    }

}
