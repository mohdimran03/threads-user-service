package com.example.userservice.services;

import com.example.userservice.dtos.UpdateUserDto;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.models.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    public void testUpdateUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setName("John Doe");
        userDto.setDob(LocalDate.of(1990, 5, 15));
        // Mocking the repository behavior
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setDob(LocalDate.of(1980, 1, 1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Act
        ResponseEntity<String> response = userService.updateUser(userDto, userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully updated user", response.getBody());
        assertEquals("John Doe", existingUser.getName()); // Verify the updated name
        assertEquals(LocalDate.of(1990, 5, 15), existingUser.getDob()); // Verify the updated dob
    }

    @Test
    public void testLoginUser_ValidUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("imran1");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("$2a$10$KULHf9DOybEUi97Osbtusec6wSTcR3sjmFWIKA1eFyHTG/qq8Gezu"); // Encrypted password for "password"

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtils.generateToken(existingUser)).thenReturn("mockToken");

        // Act
        ResponseEntity<String> response = userService.loginUser(userDto);

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful. Token: mockToken", response.getBody()); // Replace "mockToken" with actual token value
    }
    @Test
    public void testLoginUser_UserNotFound() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("nonexistent@example.com");
        userDto.setPassword("password");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = userService.loginUser(userDto);

        // Assert
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("invalid_password");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("$2a$10$Ese6xW3cMuV3ZanE1U4Y/..JLRxYDE8UxWJ7xyYD3/wTy8Q/lprQm"); // Encrypted password for "password"

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        ResponseEntity<String> response = userService.loginUser(userDto);

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody());
    }
}