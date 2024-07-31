package com.example.userservice.controllers;

import com.example.userservice.dtos.FollowingDto;
import com.example.userservice.dtos.RegisterUserDto;
import com.example.userservice.dtos.UpdateUserDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping(path = "/users")
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

//    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDto userDto, @PathVariable UUID userId) {
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        String sortBy = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "asc";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?>getuser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?>getFollowingByUser(@PathVariable UUID userId) {
        return userService.getFollowingByUser(userId);
    }

    @PostMapping("/follow")
    public ResponseEntity<?>followUser(@RequestBody FollowingDto request) {
        return userService.followUser(request);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?>unfollowUser(@RequestBody FollowingDto request) {
        return userService.unfollowUser(request);
    }

    @GetMapping("/check-username")
    public ResponseEntity<?>checkUsername(@RequestParam String username) {
        return userService.checkUsername(username);
    }
}
