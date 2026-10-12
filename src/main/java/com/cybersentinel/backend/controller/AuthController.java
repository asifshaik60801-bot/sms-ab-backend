package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.dto.AuthResponse;
import com.cybersentinel.backend.dto.LoginRequest;
import com.cybersentinel.backend.dto.RegisterRequest;
import com.cybersentinel.backend.entity.User;
import com.cybersentinel.backend.service.AuthService;
import com.cybersentinel.backend.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
        
            AuthService authService,
            JwtService jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            User user = authService.register(request);

            AuthResponse response = new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            User user = authService.login(
                    request.getEmail(),
                    request.getPassword()
            );

            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole()
            );

            return ResponseEntity.ok(
                    new LoginResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole(),
                            token
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // Response object used only for login
    static class LoginResponse {

        private Long id;
        private String name;
        private String email;
        private String role;
        private String token;

        public LoginResponse(
                Long id,
                String name,
                String email,
                String role,
                String token) {

            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.token = token;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getToken() {
            return token;
        }
    }
}