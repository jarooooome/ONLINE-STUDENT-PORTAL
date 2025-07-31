package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Section;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.InstructorService;
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
    private InstructorService instructorService;

    @Autowired
    private SectionService sectionService;

    @GetMapping("/add")
    public String showAddScheduleForm(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("instructors", instructorService.getAllInstructors());
        model.addAttribute("schedule", new Schedule());
        return "admin/addschedule";
    }

    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute("schedule") Schedule schedule,
                               @RequestParam("subjectId") Long subjectId,
                               @RequestParam("instructorId") Long instructorId,
                               @RequestParam("sectionId") Long sectionId,
                               @RequestParam("day") String day,
                               @RequestParam("startTime") String startTime,
                               @RequestParam("endTime") String endTime,
                               @RequestParam("room") String room,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validate
            if (subjectId == null) {
                throw new IllegalArgumentException("Subject is required");
            }
            if (instructorId == null) {
                throw new IllegalArgumentException("Instructor is required");
            }
            if (sectionId == null) {
                throw new IllegalArgumentException("Section is required");
            }

            // Set subject
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject == null) {
                throw new IllegalArgumentException("Invalid subject ID");
            }
            schedule.setSubject(subject);

            // Set instructor name
            String instructorName = instructorService.getInstructorById(instructorId).getFullName();
            schedule.setInstructor(instructorName);

            // Set section
            Section section = sectionService.findById(sectionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));
            schedule.setSection(section);

            // Set day, time, room
            schedule.setDay(day);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setRoom(room);

            // Save
            scheduleService.saveSchedule(schedule);

            redirectAttributes.addFlashAttribute("success", "Schedule saved successfully");
            return "redirect:/admin/subjects";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("schedule", schedule);
            return "redirect:/admin/schedules/add";
        }
    }

    @GetMapping("/list")
    public String showAllSchedules(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "admin/schedules";
    }
}
