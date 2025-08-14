package com.example.studentportal.repository;

import com.example.studentportal.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Existing method for filtering subjects by course
    List<Subject> findByCourseId(Long courseId);

    // New methods for duplicate checking (case-insensitive)
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByNameIgnoreCase(String name);
}
