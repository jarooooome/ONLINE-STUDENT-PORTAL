package com.example.studentportal.service;

import com.example.studentportal.model.Subject;
import com.example.studentportal.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void saveSubject(Subject subject) {
        subjectRepository.save(subject);
    }

    public Subject getSubjectById(Long id) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        return optionalSubject.orElse(null);
    }

    public void updateSubject(Long id, Subject updatedSubject) {
        Subject existingSubject = getSubjectById(id);
        if (existingSubject != null) {
            existingSubject.setCode(updatedSubject.getCode());
            existingSubject.setName(updatedSubject.getName());
            existingSubject.setDescription(updatedSubject.getDescription());
            subjectRepository.save(existingSubject);
        }
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
