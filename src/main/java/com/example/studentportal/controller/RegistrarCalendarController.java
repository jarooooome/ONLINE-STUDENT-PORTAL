package com.example.studentportal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registrar")
public class RegistrarCalendarController {

    // GET: Show Academic Calendar (view only)
    @GetMapping("/calendar")
    public String viewAcademicCalendar(Model model) {
        // If you want to pass events later, you can add model.addAttribute("events", ...);
        return "registrar/registrar-academic-calendar"; // looks for academic-calendar.html inside templates/registrar/
    }
}
