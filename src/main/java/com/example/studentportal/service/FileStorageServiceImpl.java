package com.example.studentportal.service;

import com.example.studentportal.model.UploadedFile;
import com.example.studentportal.repository.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    public FileStorageServiceImpl() {
        this.fileStorageLocation = Paths.get("uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public UploadedFile storeFile(MultipartFile file, String title, String description,
                                  Long scheduleId, Long professorId) {
        try {
            // Generate unique filename
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String storedFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create and save file metadata
            UploadedFile uploadedFile = new UploadedFile(
                    title,
                    description,
                    originalFileName,
                    storedFileName,
                    file.getContentType(),
                    file.getSize(),
                    scheduleId,
                    professorId
            );

            return uploadedFileRepository.save(uploadedFile);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    @Override
    public List<UploadedFile> getFilesByScheduleId(Long scheduleId) {
        return uploadedFileRepository.findByScheduleIdOrderByUploadDateDesc(scheduleId);
    }

    @Override
    public UploadedFile getFileById(Long id) {
        return uploadedFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

    @Override
    public void deleteFile(Long id) {
        UploadedFile file = getFileById(id);
        try {
            // Delete physical file
            Path filePath = this.fileStorageLocation.resolve(file.getStoredFileName()).normalize();
            Files.deleteIfExists(filePath);

            // Delete database record
            uploadedFileRepository.deleteById(id);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + file.getOriginalFileName(), ex);
        }
    }

    @Override
    public byte[] loadFileAsBytes(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("File not found: " + filename, ex);
        }
    }
}