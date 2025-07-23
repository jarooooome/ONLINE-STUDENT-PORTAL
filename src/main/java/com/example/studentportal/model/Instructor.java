package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "instructors")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId; // Optional: internal reference
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String department;
    private boolean active = true;

    // Optional profile photo field
    private String profilePhoto; // path or filename



    // Computed field (optional)
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
