package com.example.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grade_submissions")
public class GradeSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "final_rating", nullable = false)
    private Double finalRating;

    @Column(name = "submitted_by", nullable = false)
    private Long submittedBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // SUBMITTED, APPROVED, REJECTED

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "academic_term", length = 20)
    private String academicTerm;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    // Constructors
    public GradeSubmission() {
        this.submissionDate = LocalDateTime.now();
        this.status = "SUBMITTED";
    }

    public GradeSubmission(Long scheduleId, String studentId, Double finalRating,
                           Long submittedBy, String academicTerm, String academicYear) {
        this();
        this.scheduleId = scheduleId;
        this.studentId = studentId;
        this.finalRating = finalRating;
        this.submittedBy = submittedBy;
        this.academicTerm = academicTerm;
        this.academicYear = academicYear;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Double getFinalRating() {
        return finalRating;
    }

    public void setFinalRating(Double finalRating) {
        this.finalRating = finalRating;
    }

    public Long getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(Long submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getAcademicTerm() {
        return academicTerm;
    }

    public void setAcademicTerm(String academicTerm) {
        this.academicTerm = academicTerm;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    // Helper methods
    public void approve(Long approvedById) {
        this.status = "APPROVED";
        this.approvalDate = LocalDateTime.now();
        this.approvedBy = approvedById;
        this.rejectionReason = null;
    }

    public void reject(Long rejectedById, String reason) {
        this.status = "REJECTED";
        this.approvalDate = LocalDateTime.now();
        this.approvedBy = rejectedById;
        this.rejectionReason = reason;
    }

    public boolean isApproved() {
        return "APPROVED".equals(this.status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }

    public boolean isSubmitted() {
        return "SUBMITTED".equals(this.status);
    }

    @Override
    public String toString() {
        return "GradeSubmission{" +
                "id=" + id +
                ", scheduleId=" + scheduleId +
                ", studentId='" + studentId + '\'' +
                ", finalRating=" + finalRating +
                ", status='" + status + '\'' +
                ", submissionDate=" + submissionDate +
                '}';
    }
}
