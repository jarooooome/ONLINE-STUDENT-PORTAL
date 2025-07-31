package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.model.Section;
import com.example.studentportal.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final ActivityLogService activityLogService;
    private final SectionService sectionService;

    public AdminController(UserService userService,
                           CourseService courseService,
                           ActivityLogService activityLogService,
                           SectionService sectionService) {
        this.userService = userService;
        this.courseService = courseService;
        this.activityLogService = activityLogService;
        this.sectionService = sectionService;
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
        model.addAttribute("studentCount", userService.countStudentsByActiveStatus(true));
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
        model.addAttribute("users", userService.findAllUsersWithSections());
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
        model.addAttribute("courses", courseService.findAllActiveCourses());
        return "admin/adduser";
    }

    /* ───────────────────── Handle Add User Submission ───────────────────── */
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user,
                           Principal principal,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }
        setNoCacheHeaders(response);

        try {
            // Fix: Bind section properly using section.id from form
            if ("STUDENT".equalsIgnoreCase(user.getRole())
                    && user.getSection() != null
                    && user.getSection().getId() != null) {
                Section section = sectionService.findById(user.getSection().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));
                user.setSection(section);
            }

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "User saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving user: " + e.getMessage());
            return "redirect:/admin/users/add";
        }

        return "redirect:/admin/users";
    }

    /* ───────────────────── Get Sections by Course and Year ───────────────────── */
    @GetMapping("/sections/by-course-and-year")
    @ResponseBody
    public ResponseEntity<?> getSectionsByCourseAndYear(
            @RequestParam Long courseId,
            @RequestParam Integer yearLevel) {

        if (courseId == null || yearLevel == null) {
            return ResponseEntity.badRequest().body("courseId and yearLevel are required");
        }

        List<Section> sections = sectionService.findByCourseIdAndYearLevel(courseId, yearLevel);
        return ResponseEntity.ok(sections);
    }

    /* ───────────────────────── Utility Method ───────────────────────── */
    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}
