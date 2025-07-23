package com.example.studentportal.service;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    public void saveSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
