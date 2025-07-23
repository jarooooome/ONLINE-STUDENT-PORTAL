package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sections")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., 1A, 2B

    private int yearLevel; // e.g., 1 for 1st Year, 2 for 2nd Year, etc.

    @Column(columnDefinition = "boolean default true")
    private boolean active = true;

    // Each Section belongs to one Course (e.g., BSIT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    // Optional: One section can have many subjects assigned
    @ManyToMany
    @JoinTable(
            name = "section_subjects",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects;

    // Optional: One section can be handled by multiple instructors (e.g., per subject)
    @ManyToMany
    @JoinTable(
            name = "section_instructors",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private List<Instructor> instructors;
}
