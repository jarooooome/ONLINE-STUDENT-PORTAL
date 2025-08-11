package com.example.studentportal.model;

public enum GradeStatus {
    DRAFT("Draft", "bg-secondary"),
    VERIFIED("Verified", "bg-warning text-dark"),
    PUBLISHED("Published", "bg-success");

    private final String displayName;
    private final String badgeClass;

    GradeStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}