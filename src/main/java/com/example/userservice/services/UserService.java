package com.example.userservice.services;

import com.example.userservice.models.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.jwt.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtils jwtUtils;

    public UserService(UserRepository repository, JwtUtils jwtUtils) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    public ResponseEntity<String> signUpUser(UserDto userDto) {
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(encoder.encode(userDto.getPassword())); // Encrypt the password
        repository.save(newUser);
        return ResponseEntity.ok("User signed up successfully!");
    }

    public ResponseEntity<String> loginUser(UserDto userDto) {
        return repository.findByEmail(userDto.getEmail())
                .map(user -> authenticateUser(user, userDto.getPassword()))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    private ResponseEntity<String> authenticateUser(User user, String password) {
        if (encoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateToken(user);
            return ResponseEntity.ok("Login successful. Token: " + token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    public ResponseEntity<String> updateUser(UserDto userDto, UUID userId) {
        return repository.findById(userId)
                .map(user -> updateUserDetails(user, userDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<String> updateUserDetails(User user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setDob(userDto.getDob());
        user.setAge(userDto.getAge());
        user.setProfile_picture(userDto.getProfile_picture());
        user.set_bio(userDto.getBio());
        repository.save(user);
        return ResponseEntity.ok("Successfully updated user");
    }
}
