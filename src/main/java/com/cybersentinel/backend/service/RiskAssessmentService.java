
package com.cybersentinel.backend.service;

import com.cybersentinel.backend.entity.RiskAssessment;
import com.cybersentinel.backend.entity.SecurityEvent;
import com.cybersentinel.backend.entity.AuditLog;

import com.cybersentinel.backend.repository.RiskAssessmentRepository;
import com.cybersentinel.backend.repository.SecurityEventRepository;
import com.cybersentinel.backend.repository.AuditLogRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskRepository;
    private final SecurityEventRepository eventRepository;
    private final AuditLogRepository auditLogRepository;

    public RiskAssessmentService(
            RiskAssessmentRepository riskRepository,
            SecurityEventRepository eventRepository,
            AuditLogRepository auditLogRepository) {

        this.riskRepository = riskRepository;
        this.eventRepository = eventRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // CREATE RISK ASSESSMENT
    public RiskAssessment assessRisk(Long securityEventId) {

        SecurityEvent event = eventRepository.findById(securityEventId)
                .orElseThrow(() ->
                        new RuntimeException("Security event not found"));

        int score;
        String level;
        String recommendation;

        String severity = event.getSeverity();

        if ("CRITICAL".equalsIgnoreCase(severity)) {

            score = 100;
            level = "CRITICAL";
            recommendation =
                    "Immediate investigation and incident response required.";

        } else if ("HIGH".equalsIgnoreCase(severity)) {

            score = 75;
            level = "HIGH";
            recommendation =
                    "Investigate the event and monitor the affected system.";

        } else if ("MEDIUM".equalsIgnoreCase(severity)) {

            score = 50;
            level = "MEDIUM";
            recommendation =
                    "Review the event and continue monitoring.";

        } else {

            score = 25;
            level = "LOW";
            recommendation =
                    "Continue normal security monitoring.";
        }

        RiskAssessment assessment = new RiskAssessment();

        assessment.setSecurityEventId(securityEventId);
        assessment.setRiskScore(score);
        assessment.setRiskLevel(level);
        assessment.setRecommendation(recommendation);
        assessment.setAssessedAt(LocalDateTime.now());

        // Save risk assessment
        RiskAssessment savedAssessment =
                riskRepository.save(assessment);

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Create audit log
        AuditLog log = new AuditLog();

        log.setAction("RISK_ASSESSMENT_CREATED");

        log.setUsername(
                authentication != null
                        ? authentication.getName()
                        : "SYSTEM"
        );

        log.setDescription(
                "Risk assessment created for Security Event #" +
                securityEventId +
                " | Risk Score: " +
                score +
                " | Risk Level: " +
                level
        );

        log.setIpAddress("LOCALHOST");

        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);

        return savedAssessment;
    }

    // GET ALL RISK ASSESSMENTS
    public List<RiskAssessment> getAllAssessments() {
        return riskRepository.findAll();
    }

    // GET RISK ASSESSMENT BY ID
    public RiskAssessment getAssessmentById(Long id) {

        return riskRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Risk assessment not found"));
    }

    // DELETE RISK ASSESSMENT
    public void deleteAssessment(Long id) {

        RiskAssessment assessment =
                getAssessmentById(id);

        riskRepository.delete(assessment);
    }
}

