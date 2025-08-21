package com.example.studentportal.repository;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // ✅ Added: Find schedules by section
    List<Schedule> findBySection(Section section);
}

