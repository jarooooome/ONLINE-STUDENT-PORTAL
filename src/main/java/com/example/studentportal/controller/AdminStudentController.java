package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.CourseService;
import com.example.studentportal.service.SectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final SectionService sectionService;

    public AdminStudentController(UserService userService,
                                  CourseService courseService,
                                  SectionService sectionService) {
        this.userService = userService;
        this.courseService = courseService;
        this.sectionService = sectionService;
    }

    /* ─────────────── LIST: /admin/students ─────────────── */
    @GetMapping
    public String listStudents(@RequestParam(required = false) Long courseId,
                               @RequestParam(required = false) Integer yearLevel,
                               @RequestParam(required = false) Long sectionId,
                               Model model,
                               Principal principal) {

        List<User> students = userService.filterStudents(courseId, yearLevel, sectionId);
        model.addAttribute("students", students);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("currentAdmin", principal.getName());
        return "admin/studentrecords";
    }

    /* ─────────────── VIEW: /admin/students/{id} ────────── */
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        model.addAttribute("student", student);
        return "admin/students/view";
    }

    /* ─────────────── FORM: /admin/students/new ─────────── */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User student = new User();
        student.setRole("STUDENT");
        model.addAttribute("student", student);
        model.addAttribute("courses", courseService.findAllActiveCourses());
        return "admin/students/create";
    }

    /* ─────────────── CREATE (POST) ─────────────────────── */
    @PostMapping
    public String createStudent(@ModelAttribute("student") User student) {
        student.setRole("STUDENT");
        userService.saveUser(student);
        return "redirect:/admin/students";
    }

    /* ─────────────── DEACTIVATE ────────────────────────── */
    @PostMapping("/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id) {
        User u = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        u.setActive(false);
        userService.updateUser(u);
        return "redirect:/admin/students";
    }

    /* ─────────────── ACTIVATE ──────────────────────────── */
    @PostMapping("/{id}/activate")
    public String activateStudent(@PathVariable Long id) {
        User u = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        u.setActive(true);
        userService.updateUser(u);
        return "redirect:/admin/students";
    }
}
