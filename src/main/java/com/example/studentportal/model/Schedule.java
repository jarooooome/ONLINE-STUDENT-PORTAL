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
}