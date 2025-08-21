package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final UserService userService;

    public ProfessorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User professor = userService.findByEmail(currentUser.getUsername()).orElse(null);

        // Example attributes
        model.addAttribute("todayClassesCount", 0);      // Replace with real logic
        model.addAttribute("pendingGradesCount", 0);     // Replace with real logic
        model.addAttribute("notificationsCount", 0);     // Replace with real logic

        model.addAttribute("professor", professor);
        return "professor/professor-dashboard";
    }

    @GetMapping("/classes")
    public String myClasses(Model model) {
        // TODO: Add logic to get professor's classes
        return "professor/professor-classes"; // create a corresponding Thymeleaf template
    }

    @GetMapping("/grades")
    public String submitGrades(Model model) {
        // TODO: Add logic for grade submission
        return "professor/professor-grades"; // create a corresponding Thymeleaf template
    }

    @GetMapping("/schedule")
    public String classSchedule(Model model) {
        // TODO: Add logic to show professor's schedule
        return "professor/professor-schedule"; // create a corresponding Thymeleaf template
    }
}
