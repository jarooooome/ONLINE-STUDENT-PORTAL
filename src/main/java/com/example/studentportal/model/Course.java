package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String title;
    private String description;
    private int creditHours;

    @Column(columnDefinition = "boolean default true")
    private boolean active = true;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;
}