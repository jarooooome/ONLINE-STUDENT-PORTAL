package com.example.studentportal.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sections")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "A", "B", "C"

    @Column(name = "year_level", nullable = false)
    private Integer yearLevel;

    @Column(columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference // Breaks the infinite loop
    private Course course;

    @ManyToMany
    @JoinTable(
            name = "section_subjects",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @JsonManagedReference
    private List<Subject> subjects;

    @ManyToMany
    @JoinTable(
            name = "section_instructors",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    @JsonManagedReference
    private List<Instructor> instructors;

    // Custom toString() to avoid circular references in logs
    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", yearLevel=" + yearLevel +
                ", active=" + active +
                ", courseId=" + (course != null ? course.getId() : null) +
                '}';
    }
}