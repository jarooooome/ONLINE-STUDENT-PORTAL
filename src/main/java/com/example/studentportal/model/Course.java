package com.example.studentportal.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "courses")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // e.g. "BSIT"

    @Column(nullable = false)
    private String name; // e.g. "Information Technology"

    @Column(nullable = false)
    private String title; // e.g. "Bachelor of Science in Information Technology"

    private String description;

    @Column(name = "credit_hours")
    private int creditHours;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active = true;

    // Sections relationship
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("course-sections")
    private List<Section> sections;

    // Students relationship
    @ManyToMany(mappedBy = "enrolledCourses")
    @JsonIgnore
    private List<User> students;

    /* ───────────── Helper Methods ───────────── */

    @JsonIgnore
    public String getFullTitle() {
        return title + " (" + code + ")";
    }

    @JsonInclude
    public String getShortInfo() {
        return code + " - " + name;
    }

    // ✅ Abbreviation used in Thymeleaf (just exposes code)
    public String getAbbreviation() {
        return code;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
