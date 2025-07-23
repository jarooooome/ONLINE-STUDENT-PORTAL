package com.example.studentportal.service;

import com.example.studentportal.model.Instructor;

import java.util.List;
import java.util.Optional;

public interface InstructorService {
    List<Instructor> getAllInstructors();
    Optional<Instructor> getInstructorById(Long id);
    Instructor saveInstructor(Instructor instructor);
    Instructor updateInstructor(Long id, Instructor instructor);
    void deleteInstructor(Long id);
}
