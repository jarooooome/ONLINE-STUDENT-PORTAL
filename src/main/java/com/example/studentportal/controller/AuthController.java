package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* ───────────────────── Login Page ───────────────────── */
    @GetMapping("/login")
    public String login(@RequestParam(value = "success", required = false) String success, Model model) {
        if (success != null) {
            model.addAttribute("message", "Registration successful! You can now log in.");
        }
        return "auth/login";
    }

    /* ───────────────────── Registration Page ───────────────────── */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /* ───────────────────── Handle Registration ───────────────────── */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        user.setRole("STUDENT"); // Default role on registration
        userService.saveUser(user); // saveUser will handle default student fields
        return "redirect:/auth/login?success";
    }
}
