package com.example.studentportal.repository;

import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    // Get all class schedules for a given student
    List<ClassSchedule> findByStudent(User student);  // ✅ Replace Student with User

    // Get class schedules for a student filtered by the specific day of the week
    List<ClassSchedule> findByStudentAndDay(User student, DayOfWeek day);  // ✅ Replace Student with User
}
