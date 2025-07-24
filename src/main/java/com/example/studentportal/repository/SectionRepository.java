package com.example.studentportal.repository;

import com.example.studentportal.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Returns only active sections (used in dropdowns)
    List<Section> findByActiveTrue();

    // Returns sections by course ID (using explicit query for clarity)
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId")
    List<Section> findByCourseId(@Param("courseId") Long courseId);

    // Returns sections filtered by both course ID and year level (fixed)
    @Query("SELECT s FROM Section s WHERE s.course.id = :courseId AND s.yearLevel = :yearLevel")
    List<Section> findByCourseIdAndYearLevel(
            @Param("courseId") Long courseId,
            @Param("yearLevel") Integer yearLevel
    );

    // Find sections by name pattern
    List<Section> findByNameContainingIgnoreCase(String name);

    // Find sections by exact year level
    List<Section> findByYearLevel(Integer yearLevel);
}