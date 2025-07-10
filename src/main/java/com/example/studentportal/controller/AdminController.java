package com.example.studentportal.controller;

import com.example.studentportal.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final ActivityLogService activityLogService;

    // Constructor injection
    public AdminController(UserService userService,
                           StudentService studentService,
                           CourseService courseService,
                           ActivityLogService activityLogService) {
        this.userService = userService;
        this.studentService = studentService;
        this.courseService = courseService;
        this.activityLogService = activityLogService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        // Get counts for dashboard
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("studentCount", studentService.countActiveStudents());
        model.addAttribute("subjectCount", courseService.countActiveCourses());
        model.addAttribute("pendingActions", activityLogService.countPendingActions());

        // Get recent activities
        model.addAttribute("recentActivities", activityLogService.getRecentActivities());

        return "admin/admindashboard";
    }
}