package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "courses") // Make sure this matches your actual DB table
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. "BSIT"

    @Column(nullable = false)
    private String title; // e.g. "Bachelor of Science in Information Technology"

    private String description;

    @Column(name = "credit_hours")
    private int creditHours;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    // One course has many sections (e.g., BSIT-1A, BSIT-1B)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    // Many users (students) enrolled in many courses
    @ManyToMany(mappedBy = "courses")
    private List<User> students;
}
