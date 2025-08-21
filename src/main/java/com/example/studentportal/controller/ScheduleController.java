package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.ScheduleDay;
import com.example.studentportal.model.Section;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.SubjectService;
import com.example.studentportal.service.CourseService;
import com.example.studentportal.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SectionService sectionService;

    // ✅ Landing Page for schedules
    @GetMapping
    public String schedulesHome(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "admin/schedules";
    }

    // ✅ Show Add Schedule Form
    @GetMapping("/add")
    public String showAddScheduleForm(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("sections", sectionService.findAllActiveSections());
        model.addAttribute("schedule", new Schedule());
        return "admin/addschedule";
    }

    // ✅ Save Schedule
    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute("schedule") Schedule schedule,
                               @RequestParam("subjectId") Long subjectId,
                               @RequestParam("professor") String professor,
                               @RequestParam("sectionId") Long sectionId,
                               @RequestParam("room") String room,
                               @RequestParam("days") List<String> days,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validate inputs
            if (subjectId == null) throw new IllegalArgumentException("Subject is required");
            if (professor == null || professor.trim().isEmpty()) throw new IllegalArgumentException("Professor is required");
            if (sectionId == null) throw new IllegalArgumentException("Section is required");
            if (days == null || days.isEmpty()) throw new IllegalArgumentException("At least one day is required");
            if (startTime == null || startTime.isEmpty()) throw new IllegalArgumentException("Start time is required");
            if (endTime == null || endTime.isEmpty()) throw new IllegalArgumentException("End time is required");

            // Set subject
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) throw new IllegalArgumentException("Invalid subject ID");
            schedule.setSubject(subject);

            // Set professor
            schedule.setProfessor(professor.trim());

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
            return "redirect:/admin/schedules";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("schedule", schedule);
            return "redirect:/admin/schedules/add";
        }
    }

    // ✅ List All Schedules
    @GetMapping("/list")
    public String listSchedules(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "admin/schedules";
    }

    // ✅ Delete Schedule
    @GetMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("success", "Schedule deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete schedule: " + e.getMessage());
        }
        return "redirect:/admin/schedules";
    }
}
