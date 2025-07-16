package com.example.studentportal.service;

import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.User;
import com.example.studentportal.repository.ClassScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class ClassScheduleService {

    private final ClassScheduleRepository classScheduleRepository;

    public ClassScheduleService(ClassScheduleRepository classScheduleRepository) {
        this.classScheduleRepository = classScheduleRepository;
    }

    // Save a new schedule
    public ClassSchedule saveSchedule(ClassSchedule schedule) {
        return classScheduleRepository.save(schedule);
    }

    // Get all schedules for a specific student
    public List<ClassSchedule> getScheduleForStudent(User student) {
        return classScheduleRepository.findByStudent(student);
    }

    // Get today’s schedule for a student
    public List<ClassSchedule> getScheduleForStudentByDay(User student, DayOfWeek day) {
        return classScheduleRepository.findByStudentAndDay(student, day);
    }
}
