package com.example.studentportal.controller;

import com.example.studentportal.model.UploadedFile;
import com.example.studentportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileDownloadController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/professor/download-material/{id}")
    public ResponseEntity<ByteArrayResource> downloadMaterialProfessor(@PathVariable Long id, Authentication authentication) {
        return downloadMaterial(id, authentication);
    }

    @GetMapping("/student/download-material/{id}")
    public ResponseEntity<ByteArrayResource> downloadMaterialStudent(@PathVariable Long id, Authentication authentication) {
        return downloadMaterial(id, authentication);
    }

    private ResponseEntity<ByteArrayResource> downloadMaterial(Long id, Authentication authentication) {
        try {
            UploadedFile file = fileStorageService.getFileById(id);

            // You could add additional authorization checks here
            // For example, verify the student is enrolled in the class
            // or the professor teaches the class

            byte[] fileContent = fileStorageService.loadFileAsBytes(file.getStoredFileName());
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .contentLength(fileContent.length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}