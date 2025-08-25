package com.example.studentportal.repository;

import com.example.studentportal.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findByScheduleIdOrderByUploadDateDesc(Long scheduleId);
    List<UploadedFile> findByProfessorId(Long professorId);
}