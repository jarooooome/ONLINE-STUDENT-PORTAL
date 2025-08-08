package com.example.studentportal.controller;

import com.example.studentportal.model.Announcement;
import com.example.studentportal.service.AnnouncementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;

    public AdminAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // Show the form
    @GetMapping("/addannouncement")
    public String showAddAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "admin/addannouncement";
    }

    // Process form submission
    @PostMapping("/addannouncement")
    public String addAnnouncement(@ModelAttribute Announcement announcement,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/addannouncement";
        }

        announcementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement added successfully!");
        return "redirect:/admin/dashboard";
    }
}