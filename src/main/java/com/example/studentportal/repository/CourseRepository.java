package com.example.studentportal.repository;

import com.example.studentportal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    long countByActiveTrue();
    List<Course> findByActiveTrue();  // Add this method
}