package com.example.studentportal.repository;

import com.example.studentportal.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // ✅ Returns only active sections (used in dropdowns like adduser.html)
    List<Section> findByActiveTrue();

    // ✅ Optional: get sections by course if needed
    List<Section> findByCourseId(Long courseId);
}