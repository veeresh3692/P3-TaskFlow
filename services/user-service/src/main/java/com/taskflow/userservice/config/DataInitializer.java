package com.taskflow.userservice.config;

import com.taskflow.common.enums.Role;
import com.taskflow.userservice.entity.User;
import com.taskflow.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(new User("System Administrator", "admin@taskflow.com", passwordEncoder.encode("password123"), Role.ADMIN));
        userRepository.save(new User("Sarah Manager", "manager@taskflow.com", passwordEncoder.encode("password123"), Role.MANAGER));
        userRepository.save(new User("Alex Developer", "member@taskflow.com", passwordEncoder.encode("password123"), Role.MEMBER));
    }
}
