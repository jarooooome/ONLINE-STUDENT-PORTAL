package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
public class User {

    /* ───────── Common login fields ───────── */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "STUDENT";   // ADMIN or STUDENT

    private boolean active = true;
    private String contactNumber;
    private String profilePhoto;

    /* ─────── Student-specific columns ─────── */
    private String studentId;
    private String department;
    private LocalDate enrollmentDate;

    @Column(name = "year_level")
    private String yearLevel;

    /* ───────────── New: Link to Section ───────────── */
    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    /* ───────────── Many-to-many with Course ───────────── */
    @ManyToMany
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

    /* ───────────── Security helper ───────────── */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase())
        );
    }

    /* ───────────── Helper for Thymeleaf display ───────────── */
    public String getCourseTitles() {
        if (courses == null || courses.isEmpty()) {
            return "None";
        }
        return courses.stream()
                .map(Course::getTitle)
                .collect(Collectors.joining(", "));
    }
}
