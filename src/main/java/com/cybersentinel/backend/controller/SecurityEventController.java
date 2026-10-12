
package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.SecurityEvent;
import com.cybersentinel.backend.service.SecurityEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security-events")
@CrossOrigin(origins = "http://localhost:5173")
public class SecurityEventController {

    private final SecurityEventService service;

    public SecurityEventController(SecurityEventService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SecurityEvent> createEvent(
            @RequestBody SecurityEvent event) {

        SecurityEvent createdEvent = service.createEvent(event);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEvent);
    }

    @GetMapping
    public ResponseEntity<List<SecurityEvent>> getAllEvents() {

        return ResponseEntity.ok(
                service.getAllEvents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityEvent> getEventById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getEventById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SecurityEvent> updateEvent(
            @PathVariable Long id,
            @RequestBody SecurityEvent event) {

        return ResponseEntity.ok(
                service.updateEvent(id, event)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(
            @PathVariable Long id) {

        service.deleteEvent(id);

        return ResponseEntity.ok(
                "Security event deleted successfully"
        );
    }
}
