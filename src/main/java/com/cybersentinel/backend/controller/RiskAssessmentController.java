package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.RiskAssessment;
import com.cybersentinel.backend.service.RiskAssessmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk-assessments")
@CrossOrigin(origins = "http://localhost:5173")
public class RiskAssessmentController {

    private final RiskAssessmentService service;

    public RiskAssessmentController(RiskAssessmentService service) {
        this.service = service;
    }

    // CREATE RISK ASSESSMENT FOR A SECURITY EVENT
    @PostMapping("/{securityEventId}")
    public ResponseEntity<RiskAssessment> assessRisk(
            @PathVariable Long securityEventId) {

        RiskAssessment assessment =
                service.assessRisk(securityEventId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assessment);
    }

    // GET ALL RISK ASSESSMENTS
    @GetMapping
    public ResponseEntity<List<RiskAssessment>> getAllAssessments() {

        return ResponseEntity.ok(
                service.getAllAssessments()
        );
    }

    // GET RISK ASSESSMENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RiskAssessment> getAssessmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getAssessmentById(id)
        );
    }

    // DELETE RISK ASSESSMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssessment(
            @PathVariable Long id) {

        service.deleteAssessment(id);

        return ResponseEntity.ok(
                "Risk assessment deleted successfully"
        );
    }
}