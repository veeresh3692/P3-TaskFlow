package com.taskflow.service;

import com.taskflow.common.enums.Role;
import com.taskflow.config.JwtUtils;
import com.taskflow.dto.AuthRequest;
import com.taskflow.dto.AuthResponse;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.entity.User;
import com.taskflow.exception.BadRequestException;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtUtils jwtUtils;

    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", 3600000);
        authService = new AuthService(userRepository, passwordEncoder, jwtUtils);

        testUser = new User("John Doe", "john@example.com", "encodedPassword", Role.MEMBER);
        testUser.setId(1L);
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123", Role.MEMBER);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("john@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123", Role.MEMBER);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        AuthRequest request = new AuthRequest("john@example.com", "password123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void testLogin_InvalidPassword_ThrowsBadCredentialsException() {
        AuthRequest request = new AuthRequest("john@example.com", "wrongPassword");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
