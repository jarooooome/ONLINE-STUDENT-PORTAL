package com.example.studentportal.repository;

import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {

    // Get all class schedules for a given student
    List<ClassSchedule> findByStudent(Student student);

    // Get class schedules for a student filtered by the specific day of the week
    List<ClassSchedule> findByStudentAndDay(Student student, DayOfWeek day);
}
