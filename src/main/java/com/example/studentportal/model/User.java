package com.example.studentportal.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String role = "STUDENT";   // ADMIN or STUDENT

    private boolean active = true;
    private String contactNumber;


    /* ─────── Student-specific columns ─────── */
    private String studentId;
    private LocalDate enrollmentDate;

    @Column(name = "year_level")
    private Integer yearLevel;

    /* ───────────── Course Relationship ───────────── */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonBackReference("user-course")
    private Course course;

    /* ───────────── Section Relationship ───────────── */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @JsonBackReference("user-section")
    private Section section;

    /* ───────────── Courses Enrollment (Many-to-many) ───────────── */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonManagedReference("user-courses")
    private List<Course> enrolledCourses;

    /* ───────────── Transient field for custom data ───────────── */
    @Transient
    @JsonIgnore
    private Map<String, Object> customFields = new HashMap<>();

    /* ───────────── Security helper ───────────── */
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase())
        );
    }

    /* ───────────── Helper methods ───────────── */
    @JsonInclude
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @JsonInclude
    public String getMainCourseName() {
        return course != null ? course.getName() : "Not assigned";
    }

    @JsonInclude
    public String getSectionName() {
        return section != null ? section.getName() : "Not assigned";
    }

    @JsonIgnore
    public String getCourseTitles() {
        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return "None";
        }
        return enrolledCourses.stream()
                .map(Course::getTitle)
                .collect(Collectors.joining(", "));
    }

    /* ───────────── Custom toString ───────────── */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}