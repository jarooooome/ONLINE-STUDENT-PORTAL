package com.example.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "subject_id", "semester"})
})
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeStatus status = GradeStatus.DRAFT;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Grade() {}

    public Grade(User student, Subject subject, String semester, Double value) {
        this.student = student;
        this.subject = subject;
        this.semester = semester;
        this.value = value;
        this.status = GradeStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
        updateTimestamp();
    }

    public GradeStatus getStatus() {
        return status;
    }

    public void setStatus(GradeStatus status) {
        this.status = status;
        updateTimestamp();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Workflow convenience methods
    public void submitToRegistrar() {
        if (status == GradeStatus.DRAFT || status == GradeStatus.VERIFIED) {
            status = GradeStatus.SUBMITTED_TO_REGISTRAR;
            updateTimestamp();
        }
    }

    public void verify() {
        if (status == GradeStatus.DRAFT) {
            status = GradeStatus.VERIFIED;
            updateTimestamp();
        }
    }

    public void publish() {
        if (status == GradeStatus.VERIFIED || status == GradeStatus.SUBMITTED_TO_REGISTRAR) {
            status = GradeStatus.PUBLISHED;
            updateTimestamp();
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    public void submit() {
        if (this.status == GradeStatus.DRAFT || this.status == GradeStatus.VERIFIED) {
            this.status = GradeStatus.SUBMITTED_TO_REGISTRAR;
            updateTimestamp();
        } else {
            throw new IllegalStateException("Cannot submit grade in current status: " + this.status);
        }
    }

}
