package com.example.studentportal.repository;

import com.example.studentportal.model.Section;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for the unified <b>users</b> table.
 * <p>
 * <ul>
 *   <li>{@code findByEmail} – look‑up during login / duplicate‑check</li>
 *   <li>{@code findByStudentId} – alternative login using student ID</li>
 *   <li>{@code countByRole} – dashboard metrics (e.g. number of STUDENTs)</li>
 *   <li>{@code countByRoleAndActive} – count only active/inactive users of a specific role</li>
 *   <li>{@code findAllByRole} – list all users of a given role</li>
 *   <li>{@code findAllWithSections} – list all users with their sections (eager loading)</li>
 *   <li>{@code findByRole} – find users by role (alias for findAllByRole)</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /* ───────────── Look‑ups ───────────── */

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentId(String studentId);

    /* ───────────── Aggregates ─────────── */

    long countByRole(String role);

    long countByRoleAndActive(String role, boolean active);

    /* ───────────── Collections ────────── */

    List<User> findAllByRole(String role);   // e.g. STUDENT or ADMIN

    // Alias for findAllByRole - added for consistency
    List<User> findByRole(String role);


    // In your UserRepository.java
    List<User> findBySectionAndRole(Section section, String role);

    List<User> findBySectionId(Long sectionId);



    /* ───────────── Section Handling ────────── */

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.section")
    List<User> findAllWithSections();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.section WHERE u.role = :role")
    List<User> findAllByRoleWithSections(String role);
}