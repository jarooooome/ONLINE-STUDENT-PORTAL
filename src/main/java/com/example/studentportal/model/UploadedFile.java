package com.example.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uploaded_files")
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;

    private Long scheduleId;
    private Long professorId;

    // Constructors, getters, and setters
    public UploadedFile() {}

    public UploadedFile(String title, String description, String originalFileName,
                        String storedFileName, String fileType, Long fileSize,
                        Long scheduleId, Long professorId) {
        this.title = title;
        this.description = description;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.scheduleId = scheduleId;
        this.professorId = professorId;
        this.uploadDate = LocalDateTime.now();
    }

    // Getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getStoredFileName() { return storedFileName; }
    public void setStoredFileName(String storedFileName) { this.storedFileName = storedFileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    // Helper method to get file extension
    public String getFileExtension() {
        if (originalFileName == null) return "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        return lastDotIndex > 0 ? originalFileName.substring(lastDotIndex + 1) : "";
    }

    // Helper method to get display name
    public String getName() {
        return title != null ? title : originalFileName;
    }

    // Helper method to get appropriate Font Awesome icon class
    public String getFileIconClass() {
        if (originalFileName == null) return "fas fa-file";

        String extension = getFileExtension().toLowerCase();

        switch (extension) {
            case "pdf":
                return "fas fa-file-pdf text-danger";
            case "doc":
            case "docx":
                return "fas fa-file-word text-primary";
            case "xls":
            case "xlsx":
                return "fas fa-file-excel text-success";
            case "ppt":
            case "pptx":
                return "fas fa-file-powerpoint text-warning";
            case "zip":
            case "rar":
            case "7z":
                return "fas fa-file-archive text-secondary";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "fas fa-file-image text-info";
            case "txt":
                return "fas fa-file-alt text-secondary";
            case "mp3":
            case "wav":
            case "ogg":
                return "fas fa-file-audio text-warning";
            case "mp4":
            case "avi":
            case "mov":
                return "fas fa-file-video text-danger";
            default:
                return "fas fa-file";
        }
    }
}