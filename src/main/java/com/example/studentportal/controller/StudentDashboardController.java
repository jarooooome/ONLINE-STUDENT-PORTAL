package com.example.studentportal.controller;

import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.Notification;
import com.example.studentportal.model.User;
import com.example.studentportal.service.ClassScheduleService;
import com.example.studentportal.service.NotificationService;
import com.example.studentportal.service.UserService;
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

    private final UserService userService;
    private final NotificationService notificationService;
    private final ClassScheduleService classScheduleService;

    public StudentDashboardController(UserService userService,
                                      NotificationService notificationService,
                                      ClassScheduleService classScheduleService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.classScheduleService = classScheduleService;
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String studentEmail = principal.getName();

        // Get user and validate role
        User student = userService.findByEmail(studentEmail)
                .filter(u -> "STUDENT".equalsIgnoreCase(u.getRole()))
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + studentEmail));

        // Notifications for STUDENT
        List<Notification> notifications = notificationService.getNotificationsForRole("STUDENT");

        // Today’s class schedule
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ClassSchedule> todaysSchedules = classScheduleService.getScheduleForStudentByDay(student, today);
        String formattedDay = today.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        model.addAttribute("student", student);
        model.addAttribute("notifications", notifications);
        model.addAttribute("todaysSchedules", todaysSchedules);
        model.addAttribute("today", formattedDay);

        return "student/studentdashboard";
    }
}
