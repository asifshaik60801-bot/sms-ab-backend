package com.cybersentinel.backend.repository;

import com.cybersentinel.backend.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

}