package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    /* ───────────────────── Confirm‑Delete page ───────────────────── */
    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id,
                                Model model,
                                Principal principal,
                                HttpServletResponse response) throws IOException {

        // Redirect to login if the session expired
        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }
        setNoCacheHeaders(response);

        User user = userService.findById(id)
                .orElse(null); // ⬅ Fix: unwrap Optional

        if (user == null) {
            return "redirect:/admin/users?error=UserNotFound";
        }

        model.addAttribute("user", user);
        return "admin/delete";
    }

    /* ───────────────────── Actual deletion (POST) ─────────────────── */
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Principal principal,
                             HttpServletResponse response) throws IOException {

        if (principal == null) {
            response.sendRedirect("/auth/login");
            return null;
        }
        setNoCacheHeaders(response);

        userService.deleteById(id);
        return "redirect:/admin/users?deleted";
    }

    /* ─────────────────────────── Utility ─────────────────────────── */
    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma",        "no-cache");
        response.setHeader("Expires",       "0");
    }
}
