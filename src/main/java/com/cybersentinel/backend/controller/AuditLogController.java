package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.AuditLog;
import com.cybersentinel.backend.service.AuditLogService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AuditLog> createLog(
            @RequestBody AuditLog log) {

        AuditLog createdLog = service.createLog(log);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdLog);
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {

        return ResponseEntity.ok(
                service.getAllLogs()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getLogById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(
            @PathVariable Long id) {

        service.deleteLog(id);

        return ResponseEntity.ok(
                "Audit log deleted successfully"
        );
    }
}