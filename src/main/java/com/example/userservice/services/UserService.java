package com.example.userservice.services;

import com.example.userservice.models.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.jwt.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service layer for managing user operations like sign-up, login, and update.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtils jwtUtils;

    /**
     * Constructor for UserService.
     *
     * @param repository The user repository for database operations.
     * @param jwtUtils   Utility class for JWT token generation.
     */
    public UserService(UserRepository repository, JwtUtils jwtUtils) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Registers a new user with encrypted password.
     *
     * @param userDto Data transfer object containing user details.
     * @return ResponseEntity indicating success or failure of sign-up operation.
     */
    public ResponseEntity<String> signUpUser(UserDto userDto) {
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(encoder.encode(userDto.getPassword())); // Encrypt the password
        repository.save(newUser);
        return ResponseEntity.ok("User signed up successfully!");
    }

    /**
     * Authenticates a user based on email and password.
     *
     * @param userDto Data transfer object containing user credentials.
     * @return ResponseEntity with authentication status and JWT token on success.
     */
    public ResponseEntity<String> loginUser(UserDto userDto) {
        return repository.findByEmail(userDto.getEmail())
                .map(user -> authenticateUser(user, userDto.getPassword()))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    /**
     * Checks if the provided password matches the stored encrypted password.
     *
     * @param user     User object retrieved from database.
     * @param password Plain text password provided by user for authentication.
     * @return ResponseEntity indicating login success or failure.
     */
    private ResponseEntity<String> authenticateUser(User user, String password) {
        if (encoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateToken(user);
            return ResponseEntity.ok("Login successful. Token: " + token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    /**
     * Updates user details based on provided UserDto and userId.
     *
     * @param userDto Data transfer object containing updated user details.
     * @param userId  Unique identifier of the user to be updated.
     * @return ResponseEntity indicating success or failure of update operation.
     */
    public ResponseEntity<String> updateUser(UserDto userDto, UUID userId) {
        return repository.findById(userId)
                .map(user -> updateUserDetails(user, userDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates specific fields of the user entity.
     *
     * @param user    User entity to be updated.
     * @param userDto Data transfer object containing updated user details.
     * @return ResponseEntity indicating success of update operation.
     */
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
