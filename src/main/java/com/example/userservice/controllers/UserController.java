package com.example.userservice.controllers;

import com.example.userservice.dtos.RegisterUserDto;
import com.example.userservice.dtos.UpdateUserDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.services.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "users")
@EnableMethodSecurity
public class UserController {

    @Autowired
    UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody RegisterUserDto request) throws FirebaseAuthException {
        return userService.signUpUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        return userService.loginUser(userDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDto userDto, @PathVariable UUID userId) {
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<?>getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?>getuser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }
}
