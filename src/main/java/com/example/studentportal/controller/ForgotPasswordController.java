package com.example.studentportal.controller;

import com.example.studentportal.service.ForgotPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
public class ForgotPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);
    private final ForgotPasswordService forgotPasswordService;

    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        logger.debug("Showing forgot password page");
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        logger.debug("Processing forgot password request for email: {}", email);
        boolean emailExists = forgotPasswordService.sendResetCode(email);
        if (emailExists) {
            logger.debug("Email found, redirecting to OTP verification");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        } else {
            logger.warn("No account found for email: {}", email);
            model.addAttribute("error", "No account found with that email address.");
            return "auth/forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        logger.debug("Verifying OTP for email: {}, OTP: {}", email, otp);
        logger.debug("Verification attempt at: {}", LocalDateTime.now());

        boolean isValid = forgotPasswordService.verifyOTP(email, otp);
        if (isValid) {
            logger.debug("OTP validation successful for email: {}", email);
            model.addAttribute("email", email);
            return "auth/reset-password";
        } else {
            logger.warn("Invalid OTP for email: {}", email);
            model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                Model model) {
        logger.debug("Resetting password for email: {}", email);
        boolean success = forgotPasswordService.resetPassword(email, password);
        if (success) {
            logger.info("Password successfully reset for email: {}", email);
            model.addAttribute("success", "Password reset successfully. You can now log in.");
            return "auth/login"; // fixed: correct path so Thymeleaf can find login.html
        } else {
            logger.error("Password reset failed for email: {}", email);
            model.addAttribute("error", "Failed to reset password.");
            return "auth/reset-password";
        }
    }
}
