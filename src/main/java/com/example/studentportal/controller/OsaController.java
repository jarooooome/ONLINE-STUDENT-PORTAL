package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.model.Announcement;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.AnnouncementService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/osa")
public class OsaController {

    private final UserService userService;
    private final AnnouncementService announcementService;

    public OsaController(UserService userService, AnnouncementService announcementService) {
        this.userService = userService;
        this.announcementService = announcementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User osaUser = userService.findByEmail(currentUser.getUsername()).orElse(null);

        // Example attributes for OSA dashboard
        model.addAttribute("totalComplaints", 0);        // Replace with real logic
        model.addAttribute("pendingResolutions", 0);     // Replace with real logic
        model.addAttribute("eventsToday", 0);            // Replace with real logic
        model.addAttribute("notificationsCount", 0);     // Replace with real logic

        // Load announcements from DB
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);

        model.addAttribute("osaUser", osaUser);
        return "osa/osa-dashboard"; // path to template in templates/osa/
    }

    @GetMapping("/complaints")
    public String complaints(Model model) {
        // TODO: Add logic to list/manage student complaints
        return "osa/osa-complaints"; // create template
    }

    @GetMapping("/events")
    public String events(Model model) {
        // TODO: Add logic to manage student events
        return "osa/osa-events"; // create template
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        // TODO: Add logic for OSA reports
        return "osa/osa-reports"; // create template
    }
}
