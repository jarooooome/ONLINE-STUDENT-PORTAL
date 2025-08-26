package com.example.studentportal.controller;

import com.example.studentportal.model.AcademicEvent;
import com.example.studentportal.service.AcademicEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminCalendarController {

    private final AcademicEventService service;

    public AdminCalendarController(AcademicEventService service) {
        this.service = service;
    }

    // ---------- Pages ----------
    @GetMapping("/calendar")
    public String adminCalendarPage() {
        return "admin/admin-calendar";
    }

    // ---------- REST API for FullCalendar ----------
    // Return list of events as JSON in FullCalendar-friendly format
    @GetMapping("/api/calendar/events")
    @ResponseBody
    public List<Map<String, Object>> getEvents() {
        return service.findAll().stream().map(e -> Map.of(
                "id", e.getId(),
                "title", e.getTitle(),
                "start", e.getDate().toString(),   // ISO date -> FullCalendar accepts YYYY-MM-DD
                "allDay", true,
                "extendedProps", Map.of(
                        "eventType", e.getEventType(),
                        "description", e.getDescription()
                )
        )).collect(Collectors.toList());
    }

    // Create event (expects JSON)
    @PostMapping("/api/calendar/events")
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String eventType = (String) body.get("eventType");
            String description = (String) body.getOrDefault("description", "");
            String dateStr = (String) body.get("date"); // "2025-08-20"
            LocalDate date = LocalDate.parse(dateStr);

            AcademicEvent e = new AcademicEvent(title, date, eventType, description);
            AcademicEvent saved = service.save(e);
            return ResponseEntity.created(URI.create("/api/calendar/events/" + saved.getId())).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Update event
    @PutMapping("/api/calendar/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        AcademicEvent existing = service.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        try {
            if (body.containsKey("title")) existing.setTitle((String) body.get("title"));
            if (body.containsKey("eventType")) existing.setEventType((String) body.get("eventType"));
            if (body.containsKey("description")) existing.setDescription((String) body.get("description"));
            if (body.containsKey("date")) existing.setDate(LocalDate.parse((String) body.get("date")));

            AcademicEvent saved = service.save(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // Delete event
    @DeleteMapping("/api/calendar/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        AcademicEvent existing = service.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}