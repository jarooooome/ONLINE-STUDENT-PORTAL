package com.example.studentportal.service;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional(readOnly = true)
    public List<User> findAllUsersWithSections() {
        return userRepo.findAllWithSections();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(String role) {
        return userRepo.findAllByRole(role);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepo.count();
    }

    @Transactional(readOnly = true)
    public long countByRole(String role) {
        return userRepo.countByRole(role);
    }

    @Transactional(readOnly = true)
    public long countStudentsByActiveStatus(boolean isActive) {
        return userRepo.countByRoleAndActive("STUDENT", isActive);
    }

    /* ─────────────────────────── UPDATE / DELETE ─────────────────────────── */
    @Transactional
    public User updateUser(User user) {
        return userRepo.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    // ✅ Added generic save method for update profile/password use
    @Transactional
    public User save(User user) {
        return userRepo.save(user);
    }
}
