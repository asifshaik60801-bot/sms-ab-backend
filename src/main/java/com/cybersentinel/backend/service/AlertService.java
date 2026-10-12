
package com.cybersentinel.backend.service;

import com.cybersentinel.backend.entity.Alert;
import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.repository.AlertRepository;
import com.cybersentinel.backend.repository.AuditLogRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository repository;
    private final AuditLogRepository auditLogRepository;

    public AlertService(
            AlertRepository repository,
            AuditLogRepository auditLogRepository) {

        this.repository = repository;
        this.auditLogRepository = auditLogRepository;
    }

    // CREATE ALERT
    public Alert createAlert(Alert alert) {

        if (alert.getStatus() == null || alert.getStatus().isBlank()) {
            alert.setStatus("OPEN");
        }

        if (alert.getCreatedAt() == null) {
            alert.setCreatedAt(LocalDateTime.now());
        }

        return repository.save(alert);
    }

    // GET ALL ALERTS
    public List<Alert> getAllAlerts() {
        return repository.findAll();
    }

    // GET ALERT BY ID
    public Alert getAlertById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alert not found"));
    }

    // UPDATE ALERT
    public Alert updateAlert(Long id, Alert updatedAlert) {

        Alert existingAlert = getAlertById(id);

        String oldStatus = existingAlert.getStatus();

        existingAlert.setTitle(updatedAlert.getTitle());
        existingAlert.setSeverity(updatedAlert.getSeverity());
        existingAlert.setSource(updatedAlert.getSource());
        existingAlert.setDescription(updatedAlert.getDescription());
        existingAlert.setStatus(updatedAlert.getStatus());

        Alert savedAlert = repository.save(existingAlert);

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Create audit log only when status changes
        if (oldStatus == null
                || !oldStatus.equalsIgnoreCase(updatedAlert.getStatus())) {

            AuditLog log = new AuditLog();

            log.setAction("ALERT_STATUS_CHANGE");

            log.setUsername(
                    authentication != null
                            ? authentication.getName()
                            : "SYSTEM"
            );

            log.setDescription(
                    "Alert #" + id +
                    " status changed from " +
                    oldStatus +
                    " to " +
                    updatedAlert.getStatus()
            );

            log.setIpAddress("LOCALHOST");

            log.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(log);
        }

        return savedAlert;
    }

    // DELETE ALERT
    public void deleteAlert(Long id) {

        Alert alert = getAlertById(id);

        repository.delete(alert);
    }
}

