package com.example.studentportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  // Ensure this matches your DB column
    private String section;

    @Column(nullable = false)
    private String instructor;

    @Column(nullable = false)
    private String day;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(nullable = false)
    private String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // Transient field for display purposes only
    @Transient
    public String getTime() {
        return (this.startTime != null && this.endTime != null)
                ? this.startTime + " - " + this.endTime
                : "Not Scheduled";
    }
}