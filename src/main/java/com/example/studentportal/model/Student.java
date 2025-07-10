package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "students")
public class Student extends User {

    @Column(name = "student_id", unique = true, length = 20)
    private String studentId;

    @Column(length = 100)
    private String department;

    @Column(name = "enrollment_date")
    private String enrollmentDate; // You can use LocalDate if dates are stored in DB properly

    @Column(columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "students_courses",
            joinColumns = @JoinColumn(name = "student_id"), // this refers to the primary key of this entity
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;
}
