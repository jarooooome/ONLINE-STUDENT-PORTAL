package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.model.Student;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.StudentService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final StudentService studentService;

    public AuthController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        user.setRole("STUDENT");
        userService.registerUser(user);

        // Auto-create Student record if role is STUDENT
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            Student student = new Student();
            student.setEmail(user.getEmail());
            student.setFirstName(user.getFirstName());
            student.setLastName(user.getLastName());
            student.setStudentId(generateStudentId());
            student.setDepartment("Undeclared");
            student.setEnrollmentDate(String.valueOf(LocalDate.now()));
            student.setActive(true);

            studentService.createStudent(student);
        }

        return "redirect:/auth/login?success";
    }

    // Simple student ID generator
    private String generateStudentId() {
        return "S-" + LocalDate.now().getYear() + "-" + (int)(Math.random() * 9000 + 1000);
    }
}
