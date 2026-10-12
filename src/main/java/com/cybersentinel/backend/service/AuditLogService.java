package com.cybersentinel.backend.service;

import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.repository.AuditLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public AuditLog createLog(AuditLog log) {

        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }

        return repository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return repository.findAll();
    }

    public AuditLog getLogById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Audit log not found"));
    }

    public void deleteLog(Long id) {

        AuditLog log = getLogById(id);

        repository.delete(log);
    }
}