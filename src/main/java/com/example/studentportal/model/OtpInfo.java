package com.example.studentportal.model;

import java.time.LocalDateTime;

public class OtpInfo {
    private String otp;
    private LocalDateTime createdAt;

    public OtpInfo(String otp, LocalDateTime createdAt) {
        this.otp = otp;
        this.createdAt = createdAt;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
