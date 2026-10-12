
package com.cybersentinel.backend.service;

import com.cybersentinel.backend.entity.SecurityEvent;
import com.cybersentinel.backend.entity.Alert;
import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.repository.SecurityEventRepository;
import com.cybersentinel.backend.repository.AuditLogRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityEventService {

    private final SecurityEventRepository repository;
    private final AlertService alertService;
    private final AuditLogRepository auditLogRepository;

    public SecurityEventService(
            SecurityEventRepository repository,
            AlertService alertService,
            AuditLogRepository auditLogRepository) {

        this.repository = repository;
        this.alertService = alertService;
        this.auditLogRepository = auditLogRepository;
    }

    // CREATE SECURITY EVENT
    public SecurityEvent createEvent(SecurityEvent event) {

        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        if (event.getStatus() == null || event.getStatus().isBlank()) {
            event.setStatus("OPEN");
        }

        SecurityEvent savedEvent = repository.save(event);

        // AUTOMATIC ALERT GENERATION
        if ("HIGH".equalsIgnoreCase(event.getSeverity())
                || "CRITICAL".equalsIgnoreCase(event.getSeverity())) {

            Alert alert = new Alert();

            alert.setTitle(
                    "Security Alert: " + event.getEventType()
            );

            alert.setSeverity(event.getSeverity());

            alert.setSource(event.getSource());

            alert.setDescription(
                    event.getDescription()
            );

            alert.setStatus("OPEN");

            alert.setCreatedAt(LocalDateTime.now());

            alertService.createAlert(alert);
        }

        return savedEvent;
    }

    // GET ALL SECURITY EVENTS
    public List<SecurityEvent> getAllEvents() {
        return repository.findAll();
    }

    // GET SECURITY EVENT BY ID
    public SecurityEvent getEventById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Security event not found"));
    }

    // UPDATE SECURITY EVENT
    public SecurityEvent updateEvent(
            Long id,
            SecurityEvent updatedEvent) {

        SecurityEvent existingEvent = getEventById(id);

        // Store old status before updating
        String oldStatus = existingEvent.getStatus();

        existingEvent.setEventType(
                updatedEvent.getEventType()
        );

        existingEvent.setSeverity(
                updatedEvent.getSeverity()
        );

        existingEvent.setSource(
                updatedEvent.getSource()
        );

        existingEvent.setDescription(
                updatedEvent.getDescription()
        );

        existingEvent.setStatus(
                updatedEvent.getStatus()
        );

        SecurityEvent savedEvent = repository.save(existingEvent);

        // Get logged-in user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Create audit log only when status changes
        if (oldStatus == null
                || !oldStatus.equalsIgnoreCase(updatedEvent.getStatus())) {

            AuditLog log = new AuditLog();

            log.setAction("SECURITY_EVENT_STATUS_CHANGE");

            log.setUsername(
                    authentication != null
                            ? authentication.getName()
                            : "SYSTEM"
            );

            log.setDescription(
                    "Security Event #" + id +
                    " status changed from " +
                    oldStatus +
                    " to " +
                    updatedEvent.getStatus()
            );

            log.setIpAddress("LOCALHOST");

            log.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(log);
        }

        return savedEvent;
    }

    // DELETE SECURITY EVENT
    public void deleteEvent(Long id) {

        SecurityEvent event = getEventById(id);

        repository.delete(event);
    }
}

