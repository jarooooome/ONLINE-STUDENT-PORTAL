package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.User;
import com.example.studentportal.model.UploadedFile;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfessorScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    // ✅ Professor's Class Schedules Page
    @GetMapping("/professor/schedules")
    public String showProfessorSchedules(Model model, Authentication authentication) {
        try {
            // Get current professor
            String username = authentication.getName();
            User professor = userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Professor not found"));

            // Get professor's schedules using the new relationship
            List<Schedule> schedules = scheduleService.findByProfessorUser(professor);

            // If no schedules found with new relationship, try the old way
            if (schedules.isEmpty()) {
                List<Schedule> allSchedules = scheduleService.getAllSchedules();
                schedules = allSchedules.stream()
                        .filter(schedule -> {
                            String scheduleProfessor = schedule.getProfessor();
                            return scheduleProfessor != null && scheduleProfessor.equals(professor.getId().toString());
                        })
                        .collect(java.util.stream.Collectors.toList());
            }

            // For each schedule, get enrolled students and uploaded files
            for (Schedule schedule : schedules) {
                List<User> enrolledStudents = userService.findStudentsBySection(schedule.getSection());

                // Get uploaded files for this schedule
                List<UploadedFile> uploadedFiles = fileStorageService.getFilesByScheduleId(schedule.getId());

                // Ensure customFields map exists
                if (schedule.getCustomFields() == null) {
                    schedule.setCustomFields(new java.util.HashMap<>());
                }

                // Store students and files in customFields - always ensure the keys exist
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
            // Get current professor
            String username = authentication.getName();
            User professor = userService.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("Professor not found"));

            // Verify the professor has access to this schedule
            Schedule schedule = scheduleService.getScheduleById(scheduleId);
            if (schedule == null) {
                redirectAttributes.addFlashAttribute("error", "Schedule not found");
                return "redirect:/professor/schedules";
            }

            // Check if professor owns this schedule
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

            // Save the file
            fileStorageService.storeFile(file, title, description, scheduleId, professor.getId());

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully!");
            return "redirect:/professor/schedules?upload=success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
            return "redirect:/professor/schedules";
        }
    }
}