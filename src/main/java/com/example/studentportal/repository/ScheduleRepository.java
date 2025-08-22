package com.example.studentportal.repository;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Section;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // ✅ Find schedules by section
    List<Schedule> findBySection(Section section);

    // ✅ Find by professor string (for backward compatibility)
    List<Schedule> findByProfessor(String professorName);

    // ✅ Find by professor string containing (for partial matches)
    List<Schedule> findByProfessorContaining(String professorName);

    // ✅ NEW: Find by professor user ID
    List<Schedule> findByProfessorUserId(Long professorId);

    // ✅ Optional: Find by professor user entity
    List<Schedule> findByProfessorUser(User professorUser);
}