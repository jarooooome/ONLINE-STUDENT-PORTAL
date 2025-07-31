package com.example.studentportal.service;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Section;
import com.example.studentportal.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Transactional
    public void saveSchedule(Schedule schedule) {
        try {
            logger.info("Attempting to save schedule: {}", schedule);
            Schedule saved = scheduleRepository.save(schedule);
            logger.info("Schedule saved successfully with ID: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Failed to save schedule", e);
            throw e; // Re-throw to be handled by controller
        }
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    // ✅ Added: Find all schedules by section
    public List<Schedule> findBySection(Section section) {
        return scheduleRepository.findBySection(section);
    }
}
