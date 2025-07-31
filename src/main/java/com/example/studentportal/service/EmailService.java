package com.example.studentportal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification - Student Portal");
            helper.setText("""
                    <p>Hello,</p>
                    <p>Your OTP for password change is:</p>
                    <h2>%s</h2>
                    <p>This OTP will expire in a few minutes. Please do not share it with anyone.</p>
                    <p>Regards,<br>Student Portal Team</p>
                    """.formatted(otpCode), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
