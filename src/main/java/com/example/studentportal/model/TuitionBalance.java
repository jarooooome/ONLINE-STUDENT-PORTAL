package com.example.studentportal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tuition_balances",
        uniqueConstraints = @UniqueConstraint(columnNames = "student_id"))
public class TuitionBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The student this balance belongs to */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private User student;

    /** Total assessed tuition for the current term/period (or overall if you prefer) */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTuition = BigDecimal.ZERO;

    /** Sum of all recorded payments */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    /** Derived: totalTuition - amountPaid */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    /** Optional metadata */
    private LocalDateTime lastUpdated = LocalDateTime.now();

    /* ───────── Helpers ───────── */
    @PrePersist @PreUpdate
    public void onUpdate() {
        if (totalTuition == null) totalTuition = BigDecimal.ZERO;
        if (amountPaid == null) amountPaid = BigDecimal.ZERO;
        this.outstandingBalance = totalTuition.subtract(amountPaid);
        this.lastUpdated = LocalDateTime.now();
    }

    /* ───────── Getters/Setters ───────── */
    public Long getId() { return id; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public BigDecimal getTotalTuition() { return totalTuition; }
    public void setTotalTuition(BigDecimal totalTuition) { this.totalTuition = totalTuition; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
}
