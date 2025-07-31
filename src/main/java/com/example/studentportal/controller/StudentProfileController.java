package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.EmailService;
import com.example.studentportal.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
@RequestMapping("/student/profile")
public class StudentProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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

        currentStudent.setFirstName(updatedStudent.getFirstName());
        currentStudent.setLastName(updatedStudent.getLastName());
        currentStudent.setContactNumber(updatedStudent.getContactNumber());

        userService.updateUser(currentStudent);
        model.addAttribute("student", currentStudent);
        model.addAttribute("success", "Profile updated successfully.");
        return "student/profile";
    }

    // Step 1: Request OTP
    @PostMapping("/request-otp")
    public String requestOtp(HttpSession session,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        User student = userService.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("student", student);

        int otp = new Random().nextInt(900000) + 100000;
        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", student.getEmail());

        emailService.sendOtp(student.getEmail(), String.valueOf(otp));
        model.addAttribute("otpRequested", true);
        model.addAttribute("message", "A verification code has been sent to your email.");

        return "student/profile";
    }

    // Step 2: Change password with OTP
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("otp") String enteredOtp,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 HttpSession session,
                                 Model model) {

        User student = userService.findByEmail(userDetails.getUsername()).get();
        model.addAttribute("student", student);

        Object sessionOtp = session.getAttribute("otp");
        Object otpEmail = session.getAttribute("otpEmail");

        if (sessionOtp == null || otpEmail == null ||
                !otpEmail.equals(student.getEmail()) ||
                !enteredOtp.equals(sessionOtp.toString())) {
            model.addAttribute("error", "Invalid or expired OTP.");
            return "student/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "student/profile";
        }

        student.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(student);

        session.removeAttribute("otp");
        session.removeAttribute("otpEmail");

        model.addAttribute("success", "Password changed successfully.");
        return "student/profile";
    }
}
