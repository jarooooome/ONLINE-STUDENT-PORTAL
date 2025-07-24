package com.example.studentportal.service;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final ActivityLogService activityLogService;

    public UserService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       ActivityLogService activityLogService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.activityLogService = activityLogService;
    }

    /* ─────────────────────────── CREATE ─────────────────────────── */
    public User saveUser(User user) {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        user.setPassword(encoder.encode(user.getPassword()));

        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            user.setStudentId("STU" + System.currentTimeMillis());
            user.setEnrollmentDate(LocalDate.now());
            user.setActive(true);

            // Validate course is selected for students
            if (user.getCourse() == null) {
                throw new IllegalArgumentException("Course is required for students");
            }
        } else {
            user.setStudentId(null);
            user.setEnrollmentDate(null);
            user.setActive(true);
        }

        return userRepo.save(user);
    }

    /* ─────────────────────────── READ ─────────────────────────── */
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> findByRole(String role) {
        return userRepo.findAllByRole(role);
    }

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public long countUsers() {
        return userRepo.count();
    }

    public long countByRole(String role) {
        return userRepo.countByRole(role);
    }

    public long countStudentsByActiveStatus(boolean isActive) {
        return userRepo.countByRoleAndActive("STUDENT", isActive);
    }

    /* ─────────────────────────── UPDATE / DELETE ─────────────────────────── */
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }
}