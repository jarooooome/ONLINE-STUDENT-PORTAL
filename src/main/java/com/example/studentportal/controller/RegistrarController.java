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
@RequestMapping("/registrar")
public class RegistrarController {

    private final UserService userService;

    public RegistrarController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User registrar = userService.findByEmail(currentUser.getUsername()).orElse(null);

        // Example attributes
        model.addAttribute("totalStudents", 0);       // Replace with real logic
        model.addAttribute("enrolledToday", 0);       // Replace with real logic
        model.addAttribute("pendingRequests", 0);     // Replace with real logic
        model.addAttribute("notificationsCount", 0);  // Replace with real logic

        model.addAttribute("registrar", registrar);
        return "registrar/registrar-dashboard"; // updated path to subfolder
    }

    @GetMapping("/students")
    public String manageStudents(Model model) {
        // TODO: Add logic to list/manage students
        return "registrar/registrar-students"; // updated path
    }

    @GetMapping("/enrollments")
    public String enrollments(Model model) {
        // TODO: Add logic for student enrollments
        return "registrar/registrar-enrollments"; // updated path
    }

    @GetMapping("/subjects-schedules")
    public String registrarSubjectsSchedules() {
        return "registrar/registrar-subjects-schedules"; // menu page
    }



    @GetMapping("/reports")
    public String reports(Model model) {
        // TODO: Add logic for generating reports
        return "registrar/registrar-reports"; // updated path
    }
}
