package com.example.studentportal.repository;

import com.example.studentportal.model.Grade;
import com.example.studentportal.model.GradeStatus;
import com.example.studentportal.model.Subject;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudent(User student);

    List<Grade> findBySubject(Subject subject);

    List<Grade> findBySemester(String semester);

    List<Grade> findByStudentAndSubjectAndSemester(User student, Subject subject, String semester);

    @Query("SELECT g FROM Grade g WHERE "
            + "(:student IS NULL OR g.student = :student) AND "
            + "(:subject IS NULL OR g.subject = :subject) AND "
            + "(:semester IS NULL OR g.semester = :semester)")
    List<Grade> findFiltered(User student, Subject subject, String semester);

    List<Grade> findByStatus(GradeStatus status);
}
