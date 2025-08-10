package com.example.studentportal.service;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            // Only generate student ID if it's not manually provided
            if (user.getStudentId() == null || user.getStudentId().trim().isEmpty()) {
                user.setStudentId("STU" + System.currentTimeMillis());
            }

            user.setEnrollmentDate(LocalDate.now());
            user.setActive(true);

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

    @Transactional
    public User save(User user) {
        return userRepo.save(user);
    }

    /* ─────────────── FILTER FOR STUDENTS BY COURSE / YEAR / SECTION ─────────────── */
    @Transactional(readOnly = true)
    public List<User> filterStudents(Long courseId, Integer yearLevel, Long sectionId) {
        return userRepo.findAllByRole("STUDENT").stream()
                .filter(user -> courseId == null || (user.getCourse() != null && user.getCourse().getId().equals(courseId)))
                .filter(user -> yearLevel == null || (user.getYearLevel() != null && user.getYearLevel().equals(yearLevel)))
                .filter(user -> sectionId == null || (user.getSection() != null && user.getSection().getId().equals(sectionId)))
                .collect(Collectors.toList());
    }
}