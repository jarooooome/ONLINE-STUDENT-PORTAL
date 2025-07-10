package com.example.studentportal.repository;

import com.example.studentportal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    long countByActiveTrue();
    List<Student> findByActiveTrue();

    // ✅ Required to fetch logged-in student by email
    Optional<Student> findByEmail(String email);
}
