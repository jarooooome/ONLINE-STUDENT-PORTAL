package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug/auth")
public class AuthDebugController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDebugController(UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/verify")
    public String verifyCredentials(
            @RequestParam String email,
            @RequestParam String password
    ) {
        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "❌ User not found with email: " + email;
        }

        // 2. Compare passwords
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

        // 3. Check account status
        boolean isActive = true; // Modify if you have an 'active' field

        // 4. Build debug report
        return String.format("""
            ===== AUTH DEBUG =====
            Email: %s
            User Found: ✅
            Stored Password Hash: %s
            Input Password: %s
            Password Matches: %s
            Account Active: %s
            Role: %s
            ----------------------
            Conclusion: %s
            """,
                email,
                user.getPassword(),
                password,
                passwordMatches ? "✅" : "❌",
                isActive ? "✅" : "❌",
                user.getRole(),
                passwordMatches && isActive ? "✅ SHOULD WORK" : "❌ CHECK ISSUE"
        );
    }
}