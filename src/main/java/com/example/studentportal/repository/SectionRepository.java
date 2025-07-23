package com.example.studentportal.repository;

import com.example.studentportal.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCourseId(Long courseId); // ✅ Optional helper if you want to filter by course
}
