package com.example.studentportal.controller;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.User;
import com.example.studentportal.service.GradeService;
import com.example.studentportal.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentGradeController {

    private final GradeService gradeService;
    private final UserService userService;

    public StudentGradeController(GradeService gradeService, UserService userService) {
        this.gradeService = gradeService;
        this.userService = userService;
    }

    /**
     * Displays the logged-in student's grades.
     */
    @GetMapping("/grades")
    public String viewGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        // Find the logged-in student
        User student = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get only that student's grades
        List<Grade> grades = gradeService.getGradesForStudent(student);

        // Add to model for the view
        model.addAttribute("grades", grades);
        model.addAttribute("student", student);

        return "student/student-grades"; // This must be student-grades.html in templates folder
    }
}
