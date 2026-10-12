package com.cybersentinel.backend.config;

import com.cybersentinel.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        System.out.println(
                "JWT Filter -> "
                + request.getMethod()
                + " "
                + request.getRequestURI()
        );

        if (authorizationHeader == null) {
            System.out.println("JWT Filter -> No Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            System.out.println("JWT Filter -> Invalid Authorization format");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        try {

            if (!jwtService.isTokenValid(token)) {
                System.out.println("JWT Filter -> Token is INVALID");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            System.out.println(
                    "JWT Filter -> Email: "
                    + email
                    + " | Role from token: "
                    + role
            );

            if (email == null || email.isBlank()) {
                System.out.println("JWT Filter -> Email missing");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            if (role == null || role.isBlank()) {
                System.out.println("JWT Filter -> Role missing");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            role = role.trim().toUpperCase();

            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }

            String authorityName = "ROLE_" + role;

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(authorityName)
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println(
                    "JWT AUTHENTICATED SUCCESSFULLY -> "
                    + email
                    + " | "
                    + authorityName
            );

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            System.out.println(
                    "JWT Filter ERROR -> "
                    + e.getClass().getSimpleName()
                    + " : "
                    + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}