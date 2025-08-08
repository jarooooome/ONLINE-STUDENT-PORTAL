package com.example.studentportal.controller;

import com.example.studentportal.model.Announcement;
import com.example.studentportal.model.ClassSchedule;
import com.example.studentportal.model.Notification;
import com.example.studentportal.model.User;
import com.example.studentportal.service.AnnouncementService;
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
    private final AnnouncementService announcementService;  // Add this service

    public StudentDashboardController(UserService userService,
                                      NotificationService notificationService,
                                      ClassScheduleService classScheduleService,
                                      AnnouncementService announcementService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.classScheduleService = classScheduleService;
        this.announcementService = announcementService;  // Initialize here
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

        // Announcements (all for now)
        List<Announcement> announcements = announcementService.getAllAnnouncements();

        // Today’s class schedule
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ClassSchedule> todaysSchedules = classScheduleService.getScheduleForStudentByDay(student, today);
        String formattedDay = today.getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        model.addAttribute("student", student);
        model.addAttribute("notifications", notifications);
        model.addAttribute("announcements", announcements);  // add announcements to model
        model.addAttribute("todaysSchedules", todaysSchedules);
        model.addAttribute("today", formattedDay);

        return "student/studentdashboard";
    }
}
