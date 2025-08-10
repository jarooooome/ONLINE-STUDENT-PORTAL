package com.example.studentportal.service;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, OTPEntry> otpStorage = new HashMap<>();

    public ForgotPasswordService(UserRepository userRepository,
                                 JavaMailSender mailSender,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean sendResetCode(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        String otp = generateOTP();
        otpStorage.put(email, new OTPEntry(otp, LocalDateTime.now().plusMinutes(10)));
        sendEmail(email, otp);
        return true;
    }

    public boolean verifyOTP(String email, String otp) {
        OTPEntry entry = otpStorage.get(email);
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiry)) return false;
        return entry.code.equals(otp);
    }

    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStorage.remove(email);
        return true;
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otpNum = 100000 + random.nextInt(900000);
        return String.valueOf(otpNum);
    }

    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Verification Code");
        message.setText("Your verification code is: " + otp + "\n\nThis code will expire in 10 minutes.");
        mailSender.send(message);
    }

    private static class OTPEntry {
        String code;
        LocalDateTime expiry;
        OTPEntry(String code, LocalDateTime expiry) {
            this.code = code;
            this.expiry = expiry;
        }
    }
}
