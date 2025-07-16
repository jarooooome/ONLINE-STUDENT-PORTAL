package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.*;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final ActivityLogService activityLogService;

    public AdminController(UserService userService,
                           CourseService courseService,
                           ActivityLogService activityLogService) {
        this.userService = userService;
        this.courseService = courseService;
        this.activityLogService = activityLogService;
    }

    /* ───────────────────────── Admin Dashboard ───────────────────────── */
    @GetMapping("/dashboard")
    public String adminDashboard(Model model,
                                 Principal principal,
                                 HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }

        setNoCacheHeaders(response);

        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("studentCount", userService.countByRole("STUDENT"));
        model.addAttribute("subjectCount", courseService.countActiveCourses());
        model.addAttribute("pendingActions", activityLogService.countPendingActions());
        model.addAttribute("recentActivities", activityLogService.getRecentActivities());

        return "admin/admindashboard";
    }

    /* ───────────────────────── Manage Users ───────────────────────── */
    @GetMapping("/users")
    public String manageUsers(Model model,
                              Principal principal,
                              HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }

        setNoCacheHeaders(response);

        model.addAttribute("users", userService.findAllUsers());
        return "admin/manageuser";
    }

    /* ───────────────────── Show Add User Form ───────────────────── */
    @GetMapping("/users/add")
    public String showAddUserForm(Model model,
                                  Principal principal,
                                  HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }

        setNoCacheHeaders(response);

        model.addAttribute("user", new User());
        return "admin/adduser";
    }

    /* ───────────────────── Handle Add User Submission ───────────────────── */
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user,
                           @RequestParam(value = "department", required = false) String department,
                           Principal principal,
                           HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }

        setNoCacheHeaders(response);

        userService.saveUser(user, department);

        return "redirect:/admin/users";
    }

    /* ───────────────────────── Utility Method ───────────────────────── */
    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}
