package com.example.studentportal.service;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.GradeStatus;
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

        // Check if grade already exists for this student/subject/semester, throw if so
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

        // Only allow update if status is not PUBLISHED
        if (grade.getStatus() == GradeStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot edit published grades");
        }

        grade.setValue(newValue);
        grade.setUpdatedAt(java.time.LocalDateTime.now());
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
                grade.setStatus(GradeStatus.VERIFIED);
                grade.setUpdatedAt(java.time.LocalDateTime.now());
                gradeRepo.save(grade);
            }
        }
    }

    @Transactional
    public void publishGrades(List<Long> gradeIds) {
        for (Long id : gradeIds) {
            Grade grade = gradeRepo.findById(id).orElse(null);
            if (grade != null && (grade.getStatus() == GradeStatus.DRAFT || grade.getStatus() == GradeStatus.VERIFIED)) {
                grade.setStatus(GradeStatus.PUBLISHED);
                grade.setUpdatedAt(java.time.LocalDateTime.now());
                gradeRepo.save(grade);
            }
        }
    }

}
