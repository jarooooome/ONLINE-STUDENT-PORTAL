package com.example.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "academic_event")
public class AcademicEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "event_date")
    private LocalDate date;

    @Column(name = "event_type")
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String description;

    public AcademicEvent() {}

    public AcademicEvent(String title, LocalDate date, String eventType, String description) {
        this.title = title;
        this.date = date;
        this.eventType = eventType;
        this.description = description;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
