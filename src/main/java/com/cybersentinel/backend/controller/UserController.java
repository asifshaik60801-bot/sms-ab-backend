package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.entity.User;
import com.cybersentinel.backend.repository.AuditLogRepository;
import com.cybersentinel.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public UserController(
            UserRepository userRepository,
            AuditLogRepository auditLogRepository) {

        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userRepository.findAll()
        );
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestParam String role,
            Authentication authentication) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        role = role.toUpperCase();

        if (!role.equals("ADMIN")
                && !role.equals("ANALYST")
                && !role.equals("VIEWER")) {

            return ResponseEntity.badRequest()
                    .body(
                        "Invalid role. Use ADMIN, ANALYST or VIEWER."
                    );
        }

        String oldRole = user.getRole();

        user.setRole(role);

        userRepository.save(user);

        // Create audit log
        AuditLog log = new AuditLog();

        log.setAction("ROLE_CHANGE");

        log.setUsername(
                authentication.getName()
        );

        log.setDescription(
                "Changed user " +
                user.getEmail() +
                " role from " +
                oldRole +
                " to " +
                role
        );

        log.setIpAddress("LOCALHOST");

        log.setTimestamp(
                LocalDateTime.now()
        );

        auditLogRepository.save(log);

        return ResponseEntity.ok(
                "User role updated to " + role
        );
    }
}
