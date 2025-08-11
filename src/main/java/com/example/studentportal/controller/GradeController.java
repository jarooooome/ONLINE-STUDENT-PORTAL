package com.example.studentportal.controller;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.User;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.GradeService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.SubjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.web.csrf.CsrfToken;

@Controller
@RequestMapping("/admin/grades")
public class GradeController {

    private final GradeService gradeService;
    private final UserService userService;
    private final SubjectService subjectService;

    public GradeController(GradeService gradeService, UserService userService, SubjectService subjectService) {
        this.gradeService = gradeService;
        this.userService = userService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public String viewGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String semester,
            Model model,
            HttpServletRequest request  // added to get CSRF token
    ) {
        List<Grade> grades = gradeService.getGradesFiltered(studentId, subjectId, semester);

        List<User> students = userService.findByRole("STUDENT");
        List<Subject> subjects = subjectService.findAllActiveSubjects();
        List<String> semesters = grades.stream()
                .map(Grade::getSemester)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("grades", grades);
        model.addAttribute("students", students);
        model.addAttribute("subjects", subjects);
        model.addAttribute("semesters", semesters);

        // Add CSRF token to the model for Thymeleaf
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken);

        return "admin/grades"; // Updated template path
    }

    @PostMapping("/add")
    public String addGrade(
            @RequestParam Long studentId,
            @RequestParam Long subjectId,
            @RequestParam String semester,
            @RequestParam Double gradeValue,
            Model model,
            HttpServletRequest request
    ) {
        try {
            gradeService.addGrade(studentId, subjectId, semester, gradeValue);
            model.addAttribute("successMessage", "Grade added successfully.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/grades";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateGrade(@RequestBody Map<String, Object> payload) {
        try {
            Long gradeId = Long.valueOf(payload.get("gradeId").toString());
            Double value = Double.valueOf(payload.get("value").toString());
            Grade updated = gradeService.updateGradeValue(gradeId, value);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return "redirect:/admin/grades";
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String semester
    ) {
        List<Grade> filtered = gradeService.getGradesFiltered(studentId, subjectId, semester);
        List<Long> ids = filtered.stream().map(Grade::getId).collect(Collectors.toList());
        gradeService.verifyGrades(ids);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishGrades(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String semester
    ) {
        List<Grade> filtered = gradeService.getGradesFiltered(studentId, subjectId, semester);
        List<Long> ids = filtered.stream().map(Grade::getId).collect(Collectors.toList());
        gradeService.publishGrades(ids);
        return ResponseEntity.ok().build();
    }
}
