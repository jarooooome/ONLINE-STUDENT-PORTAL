package com.example.studentportal.controller;

import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.Notification;
import com.example.studentportal.model.Student;
import com.example.studentportal.service.ClassScheduleService;
import com.example.studentportal.service.NotificationService;
import com.example.studentportal.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
public class StudentDashboardController {

    private final StudentService studentService;
    private final NotificationService notificationService;
    private final ClassScheduleService classScheduleService;

    public StudentDashboardController(StudentService studentService,
                                      NotificationService notificationService,
                                      ClassScheduleService classScheduleService) {
        this.studentService = studentService;
        this.notificationService = notificationService;
        this.classScheduleService = classScheduleService;
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String studentEmail = principal.getName();

        // Get student
        Student student = studentService.getStudentByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + studentEmail));

        // Fetch real-time notifications visible to STUDENT or ALL
        List<Notification> notifications = notificationService.getNotificationsForRole("STUDENT");

        // Fetch today's class schedule
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ClassSchedule> todaysSchedules = classScheduleService.getScheduleForStudentByDay(student, today);

        // Format day name with proper casing (Thursday instead of THURSDAY)
        String formattedDay = today.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        model.addAttribute("student", student);
        model.addAttribute("notifications", notifications);
        model.addAttribute("todaysSchedules", todaysSchedules);
        model.addAttribute("today", formattedDay);

        return "student/studentdashboard";
    }
}