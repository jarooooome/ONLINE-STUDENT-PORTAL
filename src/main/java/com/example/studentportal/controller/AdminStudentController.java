package com.example.studentportal.controller;

import com.example.studentportal.model.Student;
import com.example.studentportal.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/admin/students")
public class AdminStudentController {

    private final StudentService studentService;

    public AdminStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model, Principal principal) {
        model.addAttribute("students", studentService.getActiveStudents());
        model.addAttribute("currentAdmin", principal.getName());
        return "admin/students/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID")));
        return "admin/students/view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        return "admin/students/create";
    }

    @PostMapping
    public String createStudent(@ModelAttribute Student student) {
        studentService.createStudent(student);
        return "redirect:/admin/students";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        return "redirect:/admin/students";
    }
}