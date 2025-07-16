package com.example.studentportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardRedirectController {

    @GetMapping("/dashboard")
    public String redirectToDashboard(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("STUDENT"))) {
                return "redirect:/student/dashboard";
            }
        }
        return "redirect:/auth/login";
    }
}
