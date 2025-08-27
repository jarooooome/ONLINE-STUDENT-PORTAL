package com.example.studentportal.service;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.GradeStatus;
import com.example.studentportal.model.Schedule;
import com.example.studentportal.model.Subject;
import com.example.studentportal.model.User;
import com.example.studentportal.repository.GradeRepository;
import com.example.studentportal.repository.SubjectRepository;
import com.example.studentportal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepo;
    private final UserRepository userRepo;
    private final SubjectRepository subjectRepo;

    public GradeService(GradeRepository gradeRepo, UserRepository userRepo, SubjectRepository subjectRepo) {
        this.gradeRepo = gradeRepo;
        this.userRepo = userRepo;
        this.subjectRepo = subjectRepo;
    }

    // NEW: Get all grades
    @Transactional(readOnly = true)
    public List<Grade> getAllGrades() {
        return gradeRepo.findAll();
    }

    // NEW: Get grade by ID
    @Transactional(readOnly = true)
    public Grade getGradeById(Long gradeId) {
        return gradeRepo.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found with ID: " + gradeId));
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesFiltered(Long studentId, Long subjectId, String semester) {
        User student = null;
        Subject subject = null;
        if (studentId != null) {
            student = userRepo.findById(studentId).orElse(null);
        }
        if (subjectId != null) {
            subject = subjectRepo.findById(subjectId).orElse(null);
        }
        if (semester != null && semester.trim().isEmpty()) semester = null;
        return gradeRepo.findFiltered(student, subject, semester);
    }

    @Transactional
    public Grade addGrade(Long studentId, Long subjectId, String semester, Double value) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID"));
        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subject ID"));

        List<Grade> existing = gradeRepo.findByStudentAndSubjectAndSemester(student, subject, semester);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Grade for this student, subject, and semester already exists");
        }

        Grade grade = new Grade(student, subject, semester, value);
        grade.setStatus(GradeStatus.DRAFT);
        return gradeRepo.save(grade);
    }

    @Transactional
    public Grade updateGradeValue(Long gradeId, Double newValue) {
        Grade grade = gradeRepo.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        if (grade.getStatus() == GradeStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot edit published grades");
        }

        grade.setValue(newValue); // will automatically update timestamp inside the setter if you use your current Grade model
        return gradeRepo.save(grade);
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        gradeRepo.deleteById(gradeId);
    }

    @Transactional
    public void verifyGrades(List<Long> gradeIds) {
        for (Long id : gradeIds) {
            Grade grade = gradeRepo.findById(id).orElse(null);
            if (grade != null && grade.getStatus() == GradeStatus.DRAFT) {
                grade.verify(); // use public method
                gradeRepo.save(grade);
            }
        }
    }

    @Transactional
    public void publishGrades(List<Long> gradeIds) {
        for (Long id : gradeIds) {
            Grade grade = gradeRepo.findById(id).orElse(null);
            if (grade != null && (grade.getStatus() == GradeStatus.DRAFT || grade.getStatus() == GradeStatus.VERIFIED || grade.getStatus() == GradeStatus.SUBMITTED_TO_REGISTRAR)) {
                grade.publish(); // use public method
                gradeRepo.save(grade);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Grade> getGradesForStudent(User student) {
        return gradeRepo.findByStudent(student);
    }

    // -----------------------------
    // Submit grade to registrar
    // -----------------------------
    @Transactional
    public void submitGradeToRegistrar(Long gradeId) {
        Grade grade = gradeRepo.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        // Only allow submission if status is DRAFT or VERIFIED
        if (grade.getStatus() == GradeStatus.DRAFT || grade.getStatus() == GradeStatus.VERIFIED) {
            grade.submit(); // will set status to SUBMITTED_TO_REGISTRAR
            gradeRepo.save(grade);
        }
    }

    // -----------------------------
    // SIMPLE GRADE SUBMISSION METHOD
    // -----------------------------
    @Transactional
    public void saveGrade(User professor, User student, Schedule schedule, String gradeValue, GradeStatus status) {
        // Get the subject from the schedule
        Subject subject = schedule.getSubject();
        if (subject == null) {
            throw new IllegalArgumentException("Schedule has no subject assigned");
        }

        // Use current semester (simplified - you might want to get this from schedule or configuration)
        String semester = "2023-2024-2"; // Hardcoded for simplicity

        // Convert grade value from String to Double
        Double numericGrade;
        try {
            numericGrade = Double.parseDouble(gradeValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid grade format: " + gradeValue);
        }

        // Create and save the grade
        Grade grade = new Grade(student, subject, semester, numericGrade);
        grade.setStatus(status);
        gradeRepo.save(grade);
    }

    // NEW: Get grades by status
    @Transactional(readOnly = true)
    public List<Grade> getGradesByStatus(GradeStatus status) {
        return gradeRepo.findByStatus(status);
    }

    // NEW: Get submitted grades (convenience method)
    @Transactional(readOnly = true)
    public List<Grade> getSubmittedGrades() {
        return gradeRepo.findByStatus(GradeStatus.SUBMITTED_TO_REGISTRAR);
    }
}