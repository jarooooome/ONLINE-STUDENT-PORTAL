package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.User;
import com.example.studentportal.model.UploadedFile;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.FileStorageService;
import com.example.studentportal.service.GradeService;
import com.example.studentportal.model.GradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProfessorScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GradeService gradeService;

    // ✅ Professor's Class Schedules Page
    @GetMapping("/professor/schedules")
    public String showProfessorSchedules(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User professor = userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Professor not found"));

            List<Schedule> schedules = scheduleService.findByProfessorUser(professor);

            if (schedules.isEmpty()) {
                List<Schedule> allSchedules = scheduleService.getAllSchedules();
                schedules = allSchedules.stream()
                        .filter(schedule -> {
                            String scheduleProfessor = schedule.getProfessor();
                            return scheduleProfessor != null && scheduleProfessor.equals(professor.getId().toString());
                        })
                        .toList();
            }

            for (Schedule schedule : schedules) {
                List<User> enrolledStudents = userService.findStudentsBySection(schedule.getSection());
                List<UploadedFile> uploadedFiles = fileStorageService.getFilesByScheduleId(schedule.getId());

                if (schedule.getCustomFields() == null) {
                    schedule.setCustomFields(new java.util.HashMap<>());
                }

                schedule.getCustomFields().put("enrolledStudents", enrolledStudents != null ? enrolledStudents : new ArrayList<>());
                schedule.getCustomFields().put("studentCount", enrolledStudents != null ? enrolledStudents.size() : 0);
                schedule.getCustomFields().put("uploadedFiles", uploadedFiles != null ? uploadedFiles : new ArrayList<>());
            }

            model.addAttribute("schedules", schedules);
            return "professor/schedules";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading schedules: " + e.getMessage());
            return "professor/schedules";
        }
    }

    // ✅ Handle file upload
    @PostMapping("/professor/upload-material")
    public String uploadMaterial(@RequestParam("file") MultipartFile file,
                                 @RequestParam("scheduleId") Long scheduleId,
                                 @RequestParam("fileTitle") String title,
                                 @RequestParam(value = "fileDescription", required = false) String description,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User professor = userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Professor not found"));

            Schedule schedule = scheduleService.getScheduleById(scheduleId);
            if (schedule == null) {
                redirectAttributes.addFlashAttribute("error", "Schedule not found");
                return "redirect:/professor/schedules";
            }

            boolean hasAccess = false;
            if (schedule.getProfessorUser() != null && schedule.getProfessorUser().getId().equals(professor.getId())) {
                hasAccess = true;
            } else if (schedule.getProfessor() != null && schedule.getProfessor().equals(professor.getId().toString())) {
                hasAccess = true;
            }

            if (!hasAccess) {
                redirectAttributes.addFlashAttribute("error", "You don't have access to upload files for this schedule");
                return "redirect:/professor/schedules";
            }

            fileStorageService.storeFile(file, title, description, scheduleId, professor.getId());
            redirectAttributes.addFlashAttribute("success", "File uploaded successfully!");
            return "redirect:/professor/schedules?upload=success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/professor/schedules";
        }
    }

    // ✅ Submit grade to registrar - IMPROVED VERSION
    @PostMapping("/professor/submit-grade")
    public String submitGrade(@RequestParam Long scheduleId,
                              @RequestParam Long studentId,
                              @RequestParam String grade,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            User professor = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Professor not found"));
            User student = userService.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            Schedule schedule = scheduleService.getScheduleById(scheduleId);

            if (schedule == null) {
                redirectAttributes.addFlashAttribute("error", "Schedule not found");
                return "redirect:/professor/schedules";
            }

            // Check if professor has access to this schedule
            boolean hasAccess = false;
            if (schedule.getProfessorUser() != null && schedule.getProfessorUser().getId().equals(professor.getId())) {
                hasAccess = true;
            } else if (schedule.getProfessor() != null && schedule.getProfessor().equals(professor.getId().toString())) {
                hasAccess = true;
            }

            if (!hasAccess) {
                redirectAttributes.addFlashAttribute("error", "You don't have access to submit grades for this class");
                return "redirect:/professor/schedules";
            }

            // Validate grade format
            if (!isValidGrade(grade)) {
                redirectAttributes.addFlashAttribute("error", "Invalid grade format. Please use values like 1.00, 1.25, 2.00, etc.");
                return "redirect:/professor/schedules";
            }

            // Submit the grade
            gradeService.saveGrade(professor, student, schedule, grade, GradeStatus.SUBMITTED_TO_REGISTRAR);

            redirectAttributes.addFlashAttribute("success", "Grade submitted to registrar successfully!");
            return "redirect:/professor/schedules";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit grade: " + e.getMessage());
            return "redirect:/professor/schedules";
        }
    }

    // Helper method to validate grade format
    private boolean isValidGrade(String grade) {
        try {
            double value = Double.parseDouble(grade);
            // Check if it's a valid grade value (1.00, 1.25, 1.50, ..., 3.00, 5.00)
            String[] validGrades = {"1.00", "1.25", "1.50", "1.75", "2.00", "2.25", "2.50", "2.75", "3.00", "5.00"};
            for (String validGrade : validGrades) {
                if (validGrade.equals(grade)) {
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}