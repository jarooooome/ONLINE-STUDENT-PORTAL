package com.example.studentportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardRedirectController {

    @GetMapping("/dashboard")
    public String redirectToDashboard(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (role.equals("ROLE_STUDENT")) {
                    return "redirect:/student/dashboard";
                } else if (role.equals("ROLE_CASHIER")) {
                    return "redirect:/cashier/dashboard";
                } else if (role.equals("ROLE_REGISTRAR")) {
                    return "redirect:/registrar/dashboard";
                } else if (role.equals("ROLE_PROFESSOR")) {
                    return "redirect:/professor/dashboard";
                } else if (role.equals("ROLE_OSA")) {
                    return "redirect:/osa/dashboard";
                }
            }
        }
        return "redirect:/auth/login";
    }
}