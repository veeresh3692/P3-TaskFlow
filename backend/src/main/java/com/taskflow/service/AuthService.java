package com.taskflow.service;

import com.taskflow.config.JwtUtils;
import com.taskflow.dto.AuthRequest;
import com.taskflow.dto.AuthResponse;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.dto.UserResponse;
import com.taskflow.entity.User;
import com.taskflow.exception.BadRequestException;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User saved = userRepository.save(user);
        log.info("Registered new user id={} email={}", saved.getId(), saved.getEmail());

        String token = jwtUtils.generateToken(saved.getId(), saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully id={} email={}", user.getId(), user.getEmail());
        String token = jwtUtils.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
