package com.cybersentinel.backend.repository;

import com.cybersentinel.backend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}