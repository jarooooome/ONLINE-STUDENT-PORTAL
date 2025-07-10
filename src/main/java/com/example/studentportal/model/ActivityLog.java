package com.example.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // e.g., "USER_CREATED", "GRADE_UPDATED"

    @Column(nullable = false)
    private String performedBy; // Email of admin who performed action

    @Column(nullable = false)
    private String targetEntity; // "User", "Student", "Grade" etc.

    private Long targetId; // ID of affected entity

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // Constructors
    public ActivityLog() {}

    public ActivityLog(String action, String performedBy, String targetEntity, Long targetId, String details) {
        this.action = action;
        this.performedBy = performedBy;
        this.targetEntity = targetEntity;
        this.targetId = targetId;
        this.details = details;
    }

    // Getters and Setters
    // ... (generate using your IDE or Lombok @Data)
}