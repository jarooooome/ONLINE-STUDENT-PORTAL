package com.example.studentportal.controller;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.GradeStatus;
import com.example.studentportal.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/registrar/grades")
public class RegistrarGradeController {

    @Autowired
    private GradeService gradeService;

    // ✅ View all grades with filtering
    @GetMapping
    public String viewGrades(Model model,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "semester", required = false) String semester,
                             @RequestParam(value = "studentName", required = false) String studentName) {
        try {
            // Get all grades initially
            List<Grade> allGrades = gradeService.getAllGrades(); // You'll need to add this method to GradeService

            // Apply filters
            List<Grade> filteredGrades = allGrades.stream()
                    .filter(grade -> status == null || status.isEmpty() || grade.getStatus().name().equals(status))
                    .filter(grade -> semester == null || semester.isEmpty() || grade.getSemester().equalsIgnoreCase(semester))
                    .filter(grade -> studentName == null || studentName.isEmpty() ||
                            (grade.getStudent().getFirstName() + " " + grade.getStudent().getLastName())
                                    .toLowerCase().contains(studentName.toLowerCase()))
                    .collect(Collectors.toList());

            model.addAttribute("grades", filteredGrades);
            return "registrar/grades";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading grades: " + e.getMessage());
            model.addAttribute("grades", List.of());
            return "registrar/grades";
        }
    }

    // ✅ Publish a single grade
    @PostMapping("/publish/{gradeId}")
    public String publishGrade(@PathVariable Long gradeId, RedirectAttributes redirectAttributes) {
        try {
            gradeService.publishGrades(List.of(gradeId));
            redirectAttributes.addFlashAttribute("success", "Grade published successfully! Students can now view it.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to publish grade: " + e.getMessage());
        }
        return "redirect:/registrar/grades";
    }

    // ✅ Bulk publish grades
    @PostMapping("/bulk-publish")
    public String bulkPublishGrades(RedirectAttributes redirectAttributes) {
        try {
            // Get all submitted grades
            List<Grade> submittedGrades = gradeService.getAllGrades().stream()
                    .filter(grade -> grade.getStatus() == GradeStatus.SUBMITTED_TO_REGISTRAR)
                    .collect(Collectors.toList());

            if (submittedGrades.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No submitted grades found to publish.");
                return "redirect:/registrar/grades";
            }

            List<Long> gradeIds = submittedGrades.stream()
                    .map(Grade::getId)
                    .collect(Collectors.toList());

            gradeService.publishGrades(gradeIds);
            redirectAttributes.addFlashAttribute("success", "Published " + gradeIds.size() + " grades successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to publish grades: " + e.getMessage());
        }
        return "redirect:/registrar/grades";
    }

    // ✅ Get grades by status (for API if needed)
    @GetMapping("/status/{status}")
    @ResponseBody
    public List<Grade> getGradesByStatus(@PathVariable GradeStatus status) {
        return gradeService.getAllGrades().stream()
                .filter(grade -> grade.getStatus() == status)
                .collect(Collectors.toList());
    }

    // ✅ View grade details (if you want a detail page)
    @GetMapping("/view/{gradeId}")
    public String viewGradeDetails(@PathVariable Long gradeId, Model model) {
        try {
            // You'll need to add getGradeById method to GradeService
            Grade grade = gradeService.getGradeById(gradeId);
            model.addAttribute("grade", grade);
            return "registrar/grade-details";
        } catch (Exception e) {
            model.addAttribute("error", "Grade not found: " + e.getMessage());
            return "redirect:/registrar/grades";
        }
    }
}