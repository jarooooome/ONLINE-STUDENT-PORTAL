package com.example.studentportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnore
    private Subject subject;

    // ✅ New: Proper relationship with User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private User professorUser;

    // ✅ Keep existing field for backward compatibility
    @Column(nullable = false)
    private String professor;

    @Column(nullable = false)
    private String room;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleDay> scheduleDays = new ArrayList<>();

    // Helper method to properly add schedule days
    public void addScheduleDay(String day, String startTime, String endTime) {
        ScheduleDay scheduleDay = new ScheduleDay();
        scheduleDay.setDay(day);
        scheduleDay.setStartTime(startTime);
        scheduleDay.setEndTime(endTime);
        scheduleDay.setSchedule(this);
        this.scheduleDays.add(scheduleDay);
    }

    // Ensures proper initialization
    public List<ScheduleDay> getScheduleDays() {
        if (scheduleDays == null) {
            scheduleDays = new ArrayList<>();
        }
        return scheduleDays;
    }

    // Transient method to display time
    @Transient
    public String getTime() {
        if (!getScheduleDays().isEmpty()) {
            ScheduleDay first = scheduleDays.get(0);
            return (first.getStartTime() != null && first.getEndTime() != null)
                    ? first.getStartTime() + " - " + first.getEndTime()
                    : "Not Scheduled";
        }
        return "Not Scheduled";
    }

    // ✅ Helper method to get professor name (for backward compatibility)
    @Transient
    public String getProfessorName() {
        if (professorUser != null) {
            return professorUser.getFirstName() + " " + professorUser.getLastName();
        }
        return professor; // Fallback to the string field
    }

    // ✅ Helper method to get professor ID
    @Transient
    public Long getProfessorId() {
        if (professorUser != null) {
            return professorUser.getId();
        }
        // Try to parse ID from the string field
        try {
            return Long.parseLong(professor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}