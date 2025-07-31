package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student/profile")
public class StudentProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show profile page
    @GetMapping
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = userService.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("student", student);
        return "student/profile";
    }

    // Update profile info
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("student") User updatedStudent,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User currentStudent = userService.findByEmail(userDetails.getUsername()).get();

        // Only allow editable fields
        currentStudent.setFirstName(updatedStudent.getFirstName());
        currentStudent.setLastName(updatedStudent.getLastName());
        currentStudent.setContactNumber(updatedStudent.getContactNumber());

        userService.save(currentStudent); // save updated info
        model.addAttribute("student", currentStudent);
        model.addAttribute("success", "Profile updated successfully.");
        return "student/profile";
    }

    // Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {

        User student = userService.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("student", student);

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "student/profile";
        }

        // Validate new password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "student/profile";
        }

        // Save new password
        student.setPassword(passwordEncoder.encode(newPassword));
        userService.save(student);
        model.addAttribute("success", "Password changed successfully.");
        return "student/profile";
    }
}
