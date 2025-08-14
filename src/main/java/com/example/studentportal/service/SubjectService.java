package com.example.studentportal.service;

import com.example.studentportal.model.Subject;
import com.example.studentportal.model.Course;
import com.example.studentportal.repository.SubjectRepository;
import com.example.studentportal.dto.SubjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            existingSubject.setSemester(updatedSubject.getSemester());
            subjectRepository.save(existingSubject);
        }
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public List<Subject> findAllActiveSubjects() {
        return subjectRepository.findAll().stream()
                .filter(Subject::isActive)
                .collect(Collectors.toList());
    }

    public List<SubjectDTO> findSubjectsByCourseId(Long courseId) {
        List<Subject> subjects = subjectRepository.findByCourseId(courseId);
        if (subjects == null) return List.of();

        return subjects.stream()
                .map(s -> new SubjectDTO(
                        s.getId(),
                        s.getCode(),
                        s.getName(),
                        s.getSemester()
                ))
                .collect(Collectors.toList());
    }

    // NEW METHOD: Get subjects by course (returns full Subject entities)
    public List<Subject> getSubjectsByCourse(Course course) {
        return subjectRepository.findByCourse(course);
    }
}