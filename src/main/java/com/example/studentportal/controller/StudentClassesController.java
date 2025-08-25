package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Section;
import com.example.studentportal.model.UploadedFile;
import com.example.studentportal.model.User;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StudentClassesController {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/student/classes")
    public String showStudentClasses(Model model, Authentication authentication,
                                     @RequestParam(value = "classId", required = false) Long classId) {
        try {
            // Get current student
            String username = authentication.getName();
            User student = userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            model.addAttribute("student", student);

            if (classId != null) {
                // Show specific class details
                return showClassDetails(model, classId, student);
            } else {
                // Show all classes for the student's section
                return showAllClasses(model, student);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Error loading classes: " + e.getMessage());
            return "student/classes";
        }
    }

    private String showAllClasses(Model model, User student) {
        // Get student's section
        Section studentSection = student.getSection();

        if (studentSection == null) {
            model.addAttribute("error", "You are not assigned to any section");
            return "student/classes";
        }

        // Get all schedules for the student's section
        List<Schedule> classes = scheduleService.findBySection(studentSection);

        // For each class, get professor info and uploaded files
        for (Schedule classItem : classes) {
            // Get uploaded files for this class
            List<UploadedFile> uploadedFiles = fileStorageService.getFilesByScheduleId(classItem.getId());

            // Ensure customFields map exists
            if (classItem.getCustomFields() == null) {
                classItem.setCustomFields(new java.util.HashMap<>());
            }

            // Store files in customFields
            classItem.getCustomFields().put("uploadedFiles", uploadedFiles != null ? uploadedFiles : new java.util.ArrayList<>());
        }

        model.addAttribute("classes", classes);
        return "student/classes";
    }

    private String showClassDetails(Model model, Long classId, User student) {
        // Get student's section
        Section studentSection = student.getSection();

        if (studentSection == null) {
            model.addAttribute("error", "You are not assigned to any section");
            return showAllClasses(model, student);
        }

        // Get the specific class
        Schedule selectedClass = scheduleService.getScheduleById(classId);

        if (selectedClass == null) {
            model.addAttribute("error", "Class not found");
            return showAllClasses(model, student);
        }

        // Verify the student has access to this class (same section)
        if (!selectedClass.getSection().getId().equals(studentSection.getId())) {
            model.addAttribute("error", "You don't have access to this class");
            return showAllClasses(model, student);
        }

        // Get uploaded files for this class
        List<UploadedFile> uploadedFiles = fileStorageService.getFilesByScheduleId(classId);

        // Ensure customFields map exists
        if (selectedClass.getCustomFields() == null) {
            selectedClass.setCustomFields(new java.util.HashMap<>());
        }

        // Store files in customFields
        selectedClass.getCustomFields().put("uploadedFiles", uploadedFiles != null ? uploadedFiles : new java.util.ArrayList<>());

        model.addAttribute("selectedClass", selectedClass);
        return "student/classes";
    }
}