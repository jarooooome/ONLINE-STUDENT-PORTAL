package com.example.studentportal.controller;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.User;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.GradeService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.SubjectService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentGradeController {

    private final GradeService gradeService;
    private final UserService userService;
    private final SubjectService subjectService;

    public StudentGradeController(GradeService gradeService,
                                  UserService userService,
                                  SubjectService subjectService) {
        this.gradeService = gradeService;
        this.userService = userService;
        this.subjectService = subjectService;
    }

    /**
     * Displays the logged-in student's grades - shows all subjects with grades if available
     */
    @GetMapping("/grades")
    public String viewGrades(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        try {
            // Find the logged-in student
            User student = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Get all subjects for the student's course
            List<Subject> allSubjects = subjectService.getSubjectsByCourse(student.getCourse());

            // Get student's grades
            List<Grade> studentGrades = gradeService.getGradesForStudent(student);

            // Create a map for quick grade lookup by subject ID
            Map<Long, Grade> gradeMap = studentGrades.stream()
                    .collect(Collectors.toMap(grade -> grade.getSubject().getId(), grade -> grade));

            // Create list of subjects with their grades (if any)
            List<SubjectWithGrade> subjectsWithGrades = new ArrayList<>();

            for (Subject subject : allSubjects) {
                Grade grade = gradeMap.get(subject.getId());
                subjectsWithGrades.add(new SubjectWithGrade(subject, grade));
            }

            // Calculate statistics only for published grades
            List<Grade> publishedGrades = studentGrades.stream()
                    .filter(grade -> grade.getStatus().name().equals("PUBLISHED"))
                    .collect(Collectors.toList());

            double gpa = calculateGPA(publishedGrades);
            long passedSubjects = publishedGrades.stream()
                    .filter(grade -> grade.getValue() != null && grade.getValue() < 3.0)
                    .count();
            long failedSubjects = publishedGrades.stream()
                    .filter(grade -> grade.getValue() != null && grade.getValue() >= 3.0)
                    .count();

            // Add to model for the view
            model.addAttribute("subjectsWithGrades", subjectsWithGrades);
            model.addAttribute("student", student);
            model.addAttribute("gpa", gpa);
            model.addAttribute("passedSubjects", passedSubjects);
            model.addAttribute("failedSubjects", failedSubjects);
            model.addAttribute("totalSubjects", allSubjects.size());

            return "student/student-grades";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading grades: " + e.getMessage());
            model.addAttribute("subjectsWithGrades", List.of());
            model.addAttribute("gpa", 0.0);
            model.addAttribute("passedSubjects", 0);
            model.addAttribute("failedSubjects", 0);
            model.addAttribute("totalSubjects", 0);
            return "student/student-grades";
        }
    }

    /**
     * Helper class to combine Subject and Grade
     */
    public static class SubjectWithGrade {
        private Subject subject;
        private Grade grade;

        public SubjectWithGrade(Subject subject, Grade grade) {
            this.subject = subject;
            this.grade = grade;
        }

        public Subject getSubject() { return subject; }
        public Grade getGrade() { return grade; }
        public boolean hasGrade() { return grade != null; }
        public boolean isPublished() {
            return grade != null && "PUBLISHED".equals(grade.getStatus().name());
        }
    }

    /**
     * Calculate GPA based on published grades
     */
    private double calculateGPA(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0.0;
        int count = 0;

        for (Grade grade : grades) {
            if (grade.getValue() != null) {
                totalGradePoints += grade.getValue();
                count++;
            }
        }

        return count > 0 ? totalGradePoints / count : 0.0;
    }
}