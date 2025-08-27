package com.example.studentportal.repository;

import com.example.studentportal.model.GradeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeSubmissionRepository extends JpaRepository<GradeSubmission, Long> {

    // Find all grade submissions for a specific schedule
    List<GradeSubmission> findByScheduleId(Long scheduleId);

    // Find all grade submissions for a specific student
    List<GradeSubmission> findByStudentId(String studentId);

    // Find grade submissions by status
    List<GradeSubmission> findByStatus(String status);

    // Find grade submissions by professor who submitted them
    List<GradeSubmission> findBySubmittedBy(Long submittedBy);

    // Find a specific grade submission for a student in a schedule
    Optional<GradeSubmission> findByScheduleIdAndStudentId(Long scheduleId, String studentId);

    // Find grade submissions for a schedule with specific status
    List<GradeSubmission> findByScheduleIdAndStatus(Long scheduleId, String status);

    // Find grade submissions by academic term and year
    List<GradeSubmission> findByAcademicTermAndAcademicYear(String academicTerm, String academicYear);

    // Check if a grade submission already exists for a student in a schedule
    boolean existsByScheduleIdAndStudentId(Long scheduleId, String studentId);

    // Count grade submissions by status for a schedule
    long countByScheduleIdAndStatus(Long scheduleId, String status);

    // Find latest grade submissions with pagination
    @Query("SELECT gs FROM GradeSubmission gs ORDER BY gs.submissionDate DESC")
    List<GradeSubmission> findLatestSubmissions();

    // Find grade submissions awaiting approval (submitted but not approved/rejected)
    @Query("SELECT gs FROM GradeSubmission gs WHERE gs.status = 'SUBMITTED' ORDER BY gs.submissionDate ASC")
    List<GradeSubmission> findPendingApproval();

    // Find grade submissions by multiple student IDs
    @Query("SELECT gs FROM GradeSubmission gs WHERE gs.studentId IN :studentIds")
    List<GradeSubmission> findByStudentIds(@Param("studentIds") List<String> studentIds);

    // Find grade submissions by schedule IDs
    @Query("SELECT gs FROM GradeSubmission gs WHERE gs.scheduleId IN :scheduleIds")
    List<GradeSubmission> findByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds);

    // Find grade submissions with filters for reporting
    @Query("SELECT gs FROM GradeSubmission gs WHERE " +
            "(:scheduleId IS NULL OR gs.scheduleId = :scheduleId) AND " +
            "(:studentId IS NULL OR gs.studentId = :studentId) AND " +
            "(:status IS NULL OR gs.status = :status) AND " +
            "(:academicTerm IS NULL OR gs.academicTerm = :academicTerm) AND " +
            "(:academicYear IS NULL OR gs.academicYear = :academicYear)")
    List<GradeSubmission> findWithFilters(@Param("scheduleId") Long scheduleId,
                                          @Param("studentId") String studentId,
                                          @Param("status") String status,
                                          @Param("academicTerm") String academicTerm,
                                          @Param("academicYear") String academicYear);

    // NEW: Find all grades waiting for registrar publishing
    @Query("SELECT gs FROM GradeSubmission gs WHERE gs.status = 'SUBMITTED'")
    List<GradeSubmission> findAllSubmitted();

    // NEW: Find all grades already published for students
    @Query("SELECT gs FROM GradeSubmission gs WHERE gs.status = 'PUBLISHED'")
    List<GradeSubmission> findAllPublished();
}
