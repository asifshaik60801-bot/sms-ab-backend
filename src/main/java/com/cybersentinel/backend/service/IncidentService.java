
package com.cybersentinel.backend.service;

import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.entity.Incident;
import com.cybersentinel.backend.repository.AuditLogRepository;
import com.cybersentinel.backend.repository.IncidentRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repository;
    private final AuditLogRepository auditLogRepository;

    public IncidentService(
            IncidentRepository repository,
            AuditLogRepository auditLogRepository) {

        this.repository = repository;
        this.auditLogRepository = auditLogRepository;
    }

    public Incident createIncident(Incident incident) {

        if (incident.getStatus() == null || incident.getStatus().isBlank()) {
            incident.setStatus("OPEN");
        }

        if (incident.getCreatedAt() == null) {
            incident.setCreatedAt(LocalDateTime.now());
        }

        return repository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return repository.findAll();
    }

    public Incident getIncidentById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Incident not found"));
    }

    public Incident updateIncident(Long id, Incident updatedIncident) {

        Incident existingIncident = getIncidentById(id);

        // Store old status for audit logging
        String oldStatus = existingIncident.getStatus();

        if (updatedIncident.getTitle() != null
        && !updatedIncident.getTitle().isBlank()) {
    existingIncident.setTitle(updatedIncident.getTitle());
}
        existingIncident.setDescription(updatedIncident.getDescription());

        // Preserve existing severity if no severity is provided
        if (updatedIncident.getSeverity() != null
                && !updatedIncident.getSeverity().isBlank()) {

            existingIncident.setSeverity(updatedIncident.getSeverity());
        }

        existingIncident.setStatus(updatedIncident.getStatus());

        // Preserve existing assignee if no assignee is provided
        if (updatedIncident.getAssignedTo() != null
                && !updatedIncident.getAssignedTo().isBlank()) {

            existingIncident.setAssignedTo(updatedIncident.getAssignedTo());
        }

        if ("RESOLVED".equalsIgnoreCase(updatedIncident.getStatus())) {
            existingIncident.setResolvedAt(LocalDateTime.now());
        }

        Incident savedIncident = repository.save(existingIncident);

        // Get currently logged-in user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Create audit log only when the status actually changes
        if (oldStatus == null
                || !oldStatus.equalsIgnoreCase(updatedIncident.getStatus())) {

            AuditLog log = new AuditLog();

            log.setAction("INCIDENT_STATUS_CHANGE");

            log.setUsername(
                    authentication != null
                            ? authentication.getName()
                            : "SYSTEM"
            );

            log.setDescription(
                    "Incident #" + id +
                    " status changed from " +
                    oldStatus +
                    " to " +
                    updatedIncident.getStatus()
            );

            log.setIpAddress("LOCALHOST");

            log.setTimestamp(LocalDateTime.now());

            auditLogRepository.save(log);
        }

        return savedIncident;
    }

    public void deleteIncident(Long id) {

        Incident incident = getIncidentById(id);

        repository.delete(incident);
    }
}

