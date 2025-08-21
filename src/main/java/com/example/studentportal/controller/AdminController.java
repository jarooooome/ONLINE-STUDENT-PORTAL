package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.model.Section;
import com.example.studentportal.model.Course;
import com.example.studentportal.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final SectionService sectionService;
    private final AnnouncementService announcementService;

    public AdminController(UserService userService,
                           CourseService courseService,
                           SectionService sectionService,
                           AnnouncementService announcementService) {
        this.userService = userService;
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.announcementService = announcementService;
    }

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
        model.addAttribute("announcement", new com.example.studentportal.model.Announcement());
        model.addAttribute("announcements", announcementService.getAllAnnouncements());

        return "admin/admindashboard";
    }

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

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user,
                           Principal principal,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes,
                           Model model) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }
        setNoCacheHeaders(response);

        try {
            // Only process section if user is a STUDENT
            if ("STUDENT".equalsIgnoreCase(user.getRole())) {
                if (user.getSection() != null && user.getSection().getId() != null) {
                    Section section = sectionService.findById(user.getSection().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));
                    user.setSection(section);
                }
            } else {
                // Clear student-specific fields for non-student roles
                user.setSection(null);
                user.setStudentId(null);
                user.setYearLevel(null);
                user.setCourse(null);
            }

            // Capture raw password before encoding
            String rawPassword = user.getPassword();

            userService.saveUser(user);

            // Generate QR content and file using raw password
            String qrContent = "Email: " + user.getEmail() + "\nPassword: " + rawPassword;
            String fileName = "qr_" + user.getEmail().replaceAll("[^a-zA-Z0-9]", "_") + ".png";

            // Save to both development and runtime locations
            String[] paths = {
                    "src/main/resources/static/qr/" + fileName,
                    "target/classes/static/qr/" + fileName
            };

            for (String path : paths) {
                File directory = new File(path).getParentFile();
                if (!directory.exists()) directory.mkdirs();
                generateQRCode(qrContent, 250, 250, path);
            }

            model.addAttribute("qrCodePath", "/qr/" + fileName + "?v=" + System.currentTimeMillis());
            model.addAttribute("userEmail", user.getEmail());
            model.addAttribute("userPassword", rawPassword);

            return "admin/userqr";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving user: " + e.getMessage());
            return "redirect:/admin/users/add";
        }
    }

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

    // NEW endpoint for student ID uniqueness check
    @GetMapping("/users/student-ids")
    @ResponseBody
    public ResponseEntity<List<String>> getAllStudentIds() {
        List<String> studentIds = userService.findAllUsers().stream()
                .filter(u -> u.getStudentId() != null)
                .map(u -> u.getStudentId().trim())
                .toList();
        return ResponseEntity.ok(studentIds);
    }

    /* ======================================================
       COURSE MANAGEMENT ENDPOINTS
     ====================================================== */

    // Show all courses
    @GetMapping("/courses")
    public String manageCourses(Model model,
                                Principal principal,
                                HttpServletResponse response) throws IOException {
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }
        setNoCacheHeaders(response);

        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "admin/courses"; // maps to courses.html
    }

    // Add a new course
    @PostMapping("/courses/add")
    public String addCourse(@ModelAttribute Course course,
                            RedirectAttributes redirectAttributes) {
        try {
            courseService.createCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    // Edit existing course
    @PostMapping("/courses/edit/{id}")
    public String editCourse(@PathVariable Long id,
                             @ModelAttribute Course course,
                             RedirectAttributes redirectAttributes) {
        try {
            courseService.updateCourse(id, course);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating course: " + e.getMessage());
        }
        return "redirect:/admin/courses";
    }

    /* ====================================================== */

    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }

    private void generateQRCode(String text, int width, int height, String filePath) throws Exception {
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = Paths.get(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}
