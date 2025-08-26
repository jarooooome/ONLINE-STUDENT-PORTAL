package com.example.studentportal.repository;

import com.example.studentportal.model.Subject;
import com.example.studentportal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Existing method for filtering subjects by course ID
    List<Subject> findByCourseId(Long courseId);

    // NEW METHOD: Find subjects by Course entity
    List<Subject> findByCourse(Course course);

    // Fetch subjects for all courses in a given section
    List<Subject> findByCourseIn(List<Course> courses);


    // Existing methods for duplicate checking (case-insensitive)
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
}