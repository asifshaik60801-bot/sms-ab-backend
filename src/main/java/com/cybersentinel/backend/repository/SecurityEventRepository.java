package com.cybersentinel.backend.repository;

import com.cybersentinel.backend.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {
}