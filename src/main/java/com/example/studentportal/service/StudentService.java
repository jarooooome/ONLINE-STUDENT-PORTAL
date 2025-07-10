package com.example.studentportal.service;

import com.example.studentportal.model.Student;
import com.example.studentportal.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Create a new student record
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get all students (both active and inactive)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get only active students
    public List<Student> getActiveStudents() {
        return studentRepository.findByActiveTrue();
    }

    // Find student by ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // ✅ Find student by email (used for authentication/session-based retrieval)
    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    // Update student details
    public Student updateStudent(Long id, Student studentDetails) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setStudentId(studentDetails.getStudentId());
                    student.setDepartment(studentDetails.getDepartment());
                    student.setEnrollmentDate(studentDetails.getEnrollmentDate());
                    student.setFirstName(studentDetails.getFirstName());
                    student.setLastName(studentDetails.getLastName());
                    student.setContactNumber(studentDetails.getContactNumber());
                    student.setEmail(studentDetails.getEmail());
                    return studentRepository.save(student);
                })
                .orElseGet(() -> {
                    studentDetails.setId(id);
                    return studentRepository.save(studentDetails);
                });
    }

    // Deactivate student (soft delete)
    public void deactivateStudent(Long id) {
        studentRepository.findById(id).ifPresent(student -> {
            student.setActive(false);
            studentRepository.save(student);
        });
    }

    // Activate student
    public void activateStudent(Long id) {
        studentRepository.findById(id).ifPresent(student -> {
            student.setActive(true);
            studentRepository.save(student);
        });
    }

    // Count active students
    public long countActiveStudents() {
        return studentRepository.countByActiveTrue();
    }
}
