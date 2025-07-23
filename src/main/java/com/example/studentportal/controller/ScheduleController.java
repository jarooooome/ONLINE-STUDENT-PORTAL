package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.ScheduleService;
import com.example.studentportal.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SubjectService subjectService;

    // Show form to add a new schedule
    @GetMapping("/add")
    public String showAddScheduleForm(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("schedule", new Schedule());
        return "admin/addschedule";  // Make sure this file exists
    }

    // Save the schedule and redirect to subjects
    @PostMapping("/save")
    public String saveSchedule(@ModelAttribute("schedule") Schedule schedule, @RequestParam("subjectId") Long subjectId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        if (subject != null) {
            schedule.setSubject(subject);
            scheduleService.saveSchedule(schedule);
        }
        return "redirect:/admin/subjects"; // Make sure your subject list shows schedules
    }

    // Optional: Show all schedules for management
    @GetMapping("/list")
    public String showAllSchedules(Model model) {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        model.addAttribute("schedules", schedules);
        return "admin/schedules"; // optional page
    }
}
