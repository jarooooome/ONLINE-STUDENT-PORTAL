package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final UserService userService;

    public AdminStudentController(UserService userService) {
        this.userService = userService;
    }

    /* list */
    @GetMapping
    public String listStudents(Model model, Principal principal) {
        model.addAttribute("students", userService.findByRole("STUDENT"));
        model.addAttribute("currentAdmin", principal.getName());
        return "admin/students/list";
    }

    /* view */
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        model.addAttribute("student", student);
        return "admin/students/view";
    }

    /* new form */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User student = new User();
        student.setRole("STUDENT");
        model.addAttribute("student", student);
        return "admin/students/create";
    }

    /* create */
    @PostMapping
    public String createStudent(@ModelAttribute("student") User student,
                                @RequestParam(required = false) String department) {
        student.setRole("STUDENT");
        userService.saveUser(student, department);
        return "redirect:/admin/students";
    }

    /* deactivate */
    @PostMapping("/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id) {
        User u = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        u.setActive(false);
        userService.updateUser(u);
        return "redirect:/admin/students";
    }
}
