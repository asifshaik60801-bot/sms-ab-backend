package com.cybersentinel.backend.service;

import com.cybersentinel.backend.dto.RegisterRequest;
import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.entity.User;
import com.cybersentinel.backend.repository.UserRepository;
import com.cybersentinel.backend.repository.AuditLogRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogRepository auditLogRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    public User register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("VIEWER");

        return userRepository.save(user);
    }

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        // Automatically create audit log
        AuditLog log = new AuditLog();

        log.setAction("LOGIN");

        log.setUsername(user.getEmail());

        log.setDescription(
                "User successfully logged into CyberSentinel"
        );

        log.setIpAddress("LOCALHOST");

        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);

        return user;
    }
}