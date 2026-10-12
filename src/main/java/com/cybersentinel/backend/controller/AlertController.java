package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.Alert;
import com.cybersentinel.backend.service.AlertService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertController {

    private final AlertService service;

    public AlertController(AlertService service) {
        this.service = service;
    }

    // CREATE ALERT
    @PostMapping
    public ResponseEntity<Alert> createAlert(
            @RequestBody Alert alert) {

        Alert createdAlert = service.createAlert(alert);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAlert);
    }

    // GET ALL ALERTS
    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {

        return ResponseEntity.ok(
                service.getAllAlerts()
        );
    }

    // GET ALERT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getAlertById(id)
        );
    }

    // UPDATE ALERT
    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(
            @PathVariable Long id,
            @RequestBody Alert alert) {

        return ResponseEntity.ok(
                service.updateAlert(id, alert)
        );
    }

    // DELETE ALERT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlert(
            @PathVariable Long id) {

        service.deleteAlert(id);

        return ResponseEntity.ok(
                "Alert deleted successfully"
        );
    }
}