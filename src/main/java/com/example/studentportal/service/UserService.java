package com.example.studentportal.service;

import com.example.studentportal.model.User;
import com.example.studentportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Central service for all user‑related CRUD operations.
 * <p>
 * <b>NOTE</b>: the application now keeps <em>one</em> table – <b>users</b> – that
 * contains every column needed for both admins and students
 * ( studentId, department, enrollmentDate, active …).
 */
@Service
public class UserService {

    private final UserRepository     userRepo;
    private final PasswordEncoder    encoder;
    private final ActivityLogService activityLogService;

    public UserService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       ActivityLogService activityLogService) {
        this.userRepo           = userRepo;
        this.encoder            = encoder;
        this.activityLogService = activityLogService;
    }

    /* ─────────────────────────── CREATE ─────────────────────────── */

    /**
     * Persists a new user.
     * If the role is STUDENT it also fills student‑specific columns (studentId, department, enrollmentDate, active).
     * @param department may be null; defaults to “Undeclared”
     */
    public User saveUser(User user, String department) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        user.setPassword(encoder.encode(user.getPassword()));

        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            user.setStudentId("STU" + System.currentTimeMillis());
            user.setDepartment((department == null || department.isBlank()) ? "Undeclared" : department);
            user.setEnrollmentDate(LocalDate.now());
            user.setActive(true);
        } else {
            user.setStudentId(null);
            user.setDepartment(null);
            user.setEnrollmentDate(null);
            user.setActive(true);
        }

        return userRepo.save(user);
    }

    /** Convenience overload when callers don’t supply department. */
    public User saveUser(User user) {
        return saveUser(user, null);
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

    /* ─────────────────────────── UPDATE / DELETE ─────────────────────────── */

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }
}
