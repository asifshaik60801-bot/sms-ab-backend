package com.cybersentinel.backend.controller;

import com.cybersentinel.backend.entity.Incident;
import com.cybersentinel.backend.service.IncidentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "http://localhost:5173")
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Incident> createIncident(
            @RequestBody Incident incident) {

        Incident createdIncident = service.createIncident(incident);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdIncident);
    }

    @GetMapping
    public ResponseEntity<List<Incident>> getAllIncidents() {

        return ResponseEntity.ok(
                service.getAllIncidents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getIncidentById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> updateIncident(
            @PathVariable Long id,
            @RequestBody Incident incident) {

        return ResponseEntity.ok(
                service.updateIncident(id, incident)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncident(
            @PathVariable Long id) {

        service.deleteIncident(id);

        return ResponseEntity.ok(
                "Incident deleted successfully"
        );
    }
}
