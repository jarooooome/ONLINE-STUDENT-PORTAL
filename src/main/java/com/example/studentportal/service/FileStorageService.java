package com.example.studentportal.service;

import com.example.studentportal.model.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    UploadedFile storeFile(MultipartFile file, String title, String description,
                           Long scheduleId, Long professorId);
    List<UploadedFile> getFilesByScheduleId(Long scheduleId);
    UploadedFile getFileById(Long id);
    void deleteFile(Long id);
    byte[] loadFileAsBytes(String filename);
}