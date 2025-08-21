package com.example.studentportal.controller;

import com.example.studentportal.model.Schedule;
import com.example.studentportal.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/registrar")
public class RegistrarScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * Show all schedules for Registrar role
     * URL: /registrar/schedules
     */
    @GetMapping("/schedules")
    public String viewSchedulesForRegistrar(Model model) {
        List<Schedule> schedules = scheduleRepository.findAll();
        model.addAttribute("schedules", schedules);
        return "registrar/schedules";
        // loads src/main/resources/templates/registrar/schedules.html
    }

    // Later you can add methods here for add/edit/delete schedules if needed
}
