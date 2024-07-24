package com.example.userservice.services;

import com.example.userservice.authentication.FirebaseAuthService;
import com.example.userservice.dtos.FollowingDto;
import com.example.userservice.dtos.RegisterUserDto;
import com.example.userservice.dtos.UpdateUserDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.models.Follow;
import com.example.userservice.models.User;
import com.example.userservice.repository.FollowRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.jwt.JwtUtils;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * Service layer for managing user operations like sign-up, login, and update.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final FollowRepository followRepository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtUtils jwtUtils;
    private final FirebaseAuthService firebaseAuthService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * Constructor for UserService.
     *
     * @param repository The user repository for database operations.
     * @param jwtUtils   Utility class for JWT token generation.
     */
    public UserService(
            UserRepository repository,
            JwtUtils jwtUtils,
            FirebaseAuthService firebaseAuthService,
            FollowRepository followRepository
    ) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
        this.firebaseAuthService = firebaseAuthService;
        this.followRepository = followRepository;
    }

    /**
     * Registers a new user with encrypted password using either email and password or an ID token.
     *
     * @param request object containing user details.
     * @return ResponseEntity indicating success or failure of the sign-up operation.
     * @throws IllegalArgumentException if the provided parameters are invalid.
     */
    public ResponseEntity<?> signUpUser(@RequestBody RegisterUserDto request) {
        try {
            if (request.getId_token() != null) {
                return registerThroughIdToken(request.getId_token());
            } else if (request.getEmail() != null && request.getPassword() != null) {
                return registerThroughEmailAndPassword(request.getEmail(), request.getPassword());
            } else {
                return ResponseEntity.badRequest().body("Incorrect parameters, please provide id_token or email and password");
            }
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Registers a new user using an ID token obtained from Firebase Authentication.
     *
     * @param idToken Firebase ID token used to authenticate the user.
     * @return ResponseEntity indicating success or failure of the registration operation.
     * @throws FirebaseAuthException if there is an error verifying the ID token.
     */
    private ResponseEntity<?> registerThroughIdToken(String idToken) throws FirebaseAuthException {
        FirebaseToken token = firebaseAuthService.verifyToken(idToken);
        Map<String, Object> claims = token.getClaims();
        Map<String, Object> firebaseClaims = (Map<String, Object>) claims.get("firebase");

        String email = token.getEmail();
        String uid = token.getUid();
        String signInProvider = (String) firebaseClaims.get("sign_in_provider");

        User user = new User();
        user.setEmail(email);
        user.setFirebase_uid(uid);
        user.setAuth_provider(signInProvider);

        repository.save(user);

        // Assuming you generate JWT token after registration
        String jwtToken = jwtUtils.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("token", jwtToken);

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user using email and password.
     *
     * @param email    User's email address.
     * @param password User's plain text password.
     * @return ResponseEntity indicating success or failure of the registration operation.
     */
    private ResponseEntity<?> registerThroughEmailAndPassword(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));

        repository.save(user);

        // Assuming you generate JWT token after registration
        String jwtToken = jwtUtils.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("token", jwtToken);

        return ResponseEntity.ok(response);
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
    public ResponseEntity<String> updateUser(UpdateUserDto userDto, UUID userId) {
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
    private ResponseEntity<String> updateUserDetails(User user, UpdateUserDto userDto) {
        user.setName(userDto.getName());
        user.setDob(userDto.getDob());
        user.setAge(userDto.getAge());
        user.setProfile_picture(userDto.getProfile_picture());
        user.set_bio(userDto.getBio());
        repository.save(user);
        return ResponseEntity.ok("Successfully updated user");
    }

    /**
     * Retrieves all users.
     *
     * @return ResponseEntity containing the list of all users.
     */
    public ResponseEntity<?> getAllUsers() {
        List<User> users = repository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing the user if found, or a 404 status if not found.
     */
    public ResponseEntity<?> getUser(UUID userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    /**
     * Follows a user.
     *
     * @param request The DTO containing follower and following IDs.
     * @return ResponseEntity indicating the result of the follow operation.
     */
    public ResponseEntity<?>followUser(FollowingDto request) {
        Follow existingFollow = followRepository.findFollowingByFollower(
                request.getFollowing_id(),
                request.getFollower_id()
        );
        if (existingFollow != null) {
            return ResponseEntity.ok("You are already following this user");
        } else {
            Follow follow = new Follow();
            follow.setFollower_id(request.getFollower_id());
            follow.setFollowing_id(request.getFollowing_id());

            followRepository.save(follow);
            return ResponseEntity.ok("Successfully followed user");
        }
    }

    /**
     * Unfollows a user.
     *
     * @param request The DTO containing follower and following IDs.
     * @return ResponseEntity indicating the result of the unfollow operation.
     */
    public ResponseEntity<?>unfollowUser(FollowingDto request) {
        Follow follow = followRepository.findFollowingByFollower(
                request.getFollowing_id(),
                request.getFollower_id()
        );
        followRepository.delete(follow);
        return ResponseEntity.ok("Successfully unfollowed");
    }
}
