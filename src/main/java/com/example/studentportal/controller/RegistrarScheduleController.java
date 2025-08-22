package com.example.studentportal.controller;

import com.example.studentportal.model.*;
import com.example.studentportal.dto.SubjectDTO;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.SubjectService;
import com.example.studentportal.service.CourseService;
import com.example.studentportal.service.SectionService;
import com.example.studentportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class RegistrarScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private UserService userService;

    // ✅ Landing Page for schedules
    @GetMapping("/registrar/schedules")
    public String schedulesHome(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "registrar/schedules";
    }

    // ✅ Show Add Schedule Form
    @GetMapping("/registrar/schedules/add")
    public String showAddScheduleForm(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("professors", userService.getProfessors());
        model.addAttribute("schedule", new Schedule());
        return "registrar/addschedule";
    }

    // ✅ Show Professor Schedules Page (UPDATED)
    @GetMapping("/registrar/professors")
    public String showProfessorSchedules(Model model) {
        List<User> professors = userService.getProfessors();
        List<Schedule> allSchedules = scheduleService.getAllSchedules();

        System.out.println("Total professors: " + professors.size());
        System.out.println("Total schedules: " + allSchedules.size());

        // For each professor, find their schedules
        for (User professor : professors) {
            List<Schedule> professorSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        // Try to match by professor ID stored as string
                        String scheduleProfessor = schedule.getProfessor();
                        if (scheduleProfessor != null && scheduleProfessor.equals(professor.getId().toString())) {
                            return true;
                        }

                        // Also try to match by professor user relationship (if exists)
                        if (schedule.getProfessorUser() != null && schedule.getProfessorUser().getId().equals(professor.getId())) {
                            return true;
                        }

                        return false;
                    })
                    .collect(Collectors.toList());

            System.out.println("Professor ID: " + professor.getId() + " - Name: " + professor.getFirstName() +
                    " - Found: " + professorSchedules.size() + " schedules");

            // DEBUG: Print schedule details
            for (Schedule s : professorSchedules) {
                System.out.println("  Schedule ID: " + s.getId() + ", Professor field: '" + s.getProfessor() +
                        "', ProfessorUser: " + (s.getProfessorUser() != null ? s.getProfessorUser().getId() : "null"));
            }

            Map<String, Object> customFields = new HashMap<>();
            customFields.put("schedules", professorSchedules);
            customFields.put("scheduleCount", professorSchedules.size());
            professor.setCustomFields(customFields);
        }

        model.addAttribute("professors", professors);
        return "registrar/professors";
    }

    // ✅ Temporary method to migrate professor data
    @GetMapping("/registrar/migrate-professors")
    @ResponseBody
    public String migrateProfessorData() {
        List<Schedule> allSchedules = scheduleService.getAllSchedules();
        int migrated = 0;

        for (Schedule schedule : allSchedules) {
            try {
                String professorIdStr = schedule.getProfessor();
                if (professorIdStr != null && !professorIdStr.trim().isEmpty()) {
                    Long professorId = Long.parseLong(professorIdStr);
                    User professor = userService.findById(professorId).orElse(null);

                    if (professor != null) {
                        schedule.setProfessorUser(professor);
                        scheduleService.saveSchedule(schedule);
                        migrated++;
                        System.out.println("Migrated schedule " + schedule.getId() + " to professor " + professorId);
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore if professor field is not a number
            }
        }

        return "Migrated " + migrated + " schedules";
    }

    // ✅ Debug endpoint to see all schedules
    @GetMapping("/registrar/debug/all-schedules")
    @ResponseBody
    public List<Map<String, Object>> debugAllSchedules() {
        List<Schedule> allSchedules = scheduleService.getAllSchedules();

        return allSchedules.stream().map(schedule -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", schedule.getId());
            map.put("professorField", schedule.getProfessor());
            map.put("professorUserId", schedule.getProfessorUser() != null ? schedule.getProfessorUser().getId() : null);
            map.put("subject", schedule.getSubject() != null ? schedule.getSubject().getName() : null);
            map.put("section", schedule.getSection() != null ? schedule.getSection().getName() : null);
            return map;
        }).collect(Collectors.toList());
    }

    // ✅ AJAX Endpoint: Get subjects by course (using SubjectDTO)
    @GetMapping("/registrar/schedules/subjects/by-course")
    @ResponseBody
    public ResponseEntity<List<SubjectDTO>> getSubjectsByCourse(@RequestParam Long courseId) {
        try {
            List<Subject> subjects = subjectService.getSubjectsByCourseId(courseId);

            // Convert to DTO to avoid circular references
            List<SubjectDTO> subjectDTOs = subjects.stream()
                    .map(subject -> new SubjectDTO(
                            subject.getId(),
                            subject.getCode(),
                            subject.getName(),
                            subject.getSemester()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(subjectDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ AJAX Endpoint: Get sections by course and year level
    @GetMapping("/registrar/schedules/sections/by-course-and-year")
    @ResponseBody
    public ResponseEntity<List<Section>> getSectionsByCourseAndYear(
            @RequestParam Long courseId,
            @RequestParam Integer yearLevel) {
        try {
            List<Section> sections = sectionService.findByCourseIdAndYearLevel(courseId, yearLevel);
            return ResponseEntity.ok(sections);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Save Schedule (UPDATED to use professorId)
    @PostMapping("/registrar/schedules/save")
    public String saveSchedule(@ModelAttribute("schedule") Schedule schedule,
                               @RequestParam("subjectId") Long subjectId,
                               @RequestParam("professorId") Long professorId, // Changed to professorId
                               @RequestParam("sectionId") Long sectionId,
                               @RequestParam("room") String room,
                               @RequestParam("days") List<String> days,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validate inputs
            if (subjectId == null) throw new IllegalArgumentException("Subject is required");
            if (professorId == null) throw new IllegalArgumentException("Professor is required");
            if (sectionId == null) throw new IllegalArgumentException("Section is required");
            if (days == null || days.isEmpty()) throw new IllegalArgumentException("At least one day is required");
            if (startTime == null || startTime.isEmpty()) throw new IllegalArgumentException("Start time is required");
            if (endTime == null || endTime.isEmpty()) throw new IllegalArgumentException("End time is required");

            // Set subject
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) throw new IllegalArgumentException("Invalid subject ID");
            schedule.setSubject(subject);

            // Set professor user (new relationship)
            User professor = userService.findById(professorId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid professor ID"));
            schedule.setProfessorUser(professor);

            // Also set the string field for backward compatibility
            schedule.setProfessor(professorId.toString());

            // Set section
            Section section = sectionService.findById(sectionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));
            schedule.setSection(section);

            // Set room
            schedule.setRoom(room);

            // Clear and add schedule days
            schedule.getScheduleDays().clear();
            for (String day : days) {
                ScheduleDay scheduleDay = new ScheduleDay();
                scheduleDay.setDay(day);
                scheduleDay.setStartTime(startTime);
                scheduleDay.setEndTime(endTime);
                scheduleDay.setSchedule(schedule);
                schedule.getScheduleDays().add(scheduleDay);
            }

            // Save
            scheduleService.saveSchedule(schedule);

            redirectAttributes.addFlashAttribute("success", "Schedule saved successfully.");
            return "redirect:/registrar/schedules";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/registrar/schedules/add";
        }
    }

    // ✅ Delete Schedule
    @GetMapping("/registrar/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("success", "Schedule deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete schedule: " + e.getMessage());
        }
        return "redirect:/registrar/schedules";
    }
}