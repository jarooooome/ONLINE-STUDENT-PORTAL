package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.User;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentScheduleController {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/schedule")
    public String viewSchedule(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 1. Get logged-in student with Optional check
        User student = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + userDetails.getUsername()));

        // 2. Fetch schedule by student section
        List<Schedule> schedules = scheduleService.findBySection(student.getSection());

        // 3. Set attributes
        model.addAttribute("student", student);
        model.addAttribute("schedules", schedules);
        model.addAttribute("date", LocalDate.now());
        model.addAttribute("dayOfWeek", LocalDate.now().getDayOfWeek().toString());

        return "student/class-schedule"; // Make sure your HTML is named `class-schedule.html`
    }
}
