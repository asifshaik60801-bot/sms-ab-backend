package com.cybersentinel.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long securityEventId;

    @Column(nullable = false)
    private int riskScore;

    @Column(nullable = false)
    private String riskLevel;

    private String recommendation;

    private LocalDateTime assessedAt;

    public RiskAssessment() {
    }

    public Long getId() {
        return id;
    }

    public Long getSecurityEventId() {
        return securityEventId;
    }

    public void setSecurityEventId(Long securityEventId) {
        this.securityEventId = securityEventId;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public LocalDateTime getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDateTime assessedAt) {
        this.assessedAt = assessedAt;
    }
}