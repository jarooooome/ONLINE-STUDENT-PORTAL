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
@RequestMapping("/osa")
public class OsaAnnouncementController {

    private final AnnouncementService announcementService;

    public OsaAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // Show add form
    @GetMapping("/addannouncement")
    public String showAddAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "osa/addannouncement";
    }

    // Process add form submission
    @PostMapping("/addannouncement")
    public String addAnnouncement(@Valid @ModelAttribute Announcement announcement,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "osa/addannouncement";
        }
        announcementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement added successfully!");
        return "redirect:/osa/dashboard";
    }

    // Show edit form
    @GetMapping("/editannouncement/{id}")
    public String showEditAnnouncementForm(@PathVariable Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncementById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid announcement Id:" + id));
        model.addAttribute("announcement", announcement);
        return "osa/editannouncement";
    }

    // Process edit form submission
    @PostMapping("/updateannouncement/{id}")
    public String updateAnnouncement(@PathVariable Long id,
                                     @Valid @ModelAttribute Announcement announcement,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "osa/editannouncement";
        }
        announcement.setId(id);
        announcementService.saveAnnouncement(announcement);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement updated successfully!");
        return "redirect:/osa/dashboard";
    }

    // Delete announcement
    @PostMapping("/deleteannouncement/{id}")
    public String deleteAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.deleteAnnouncement(id);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted successfully!");
        return "redirect:/osa/dashboard";
    }
}
