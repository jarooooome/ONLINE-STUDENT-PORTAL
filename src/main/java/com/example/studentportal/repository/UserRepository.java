package com.example.studentportal.repository;

import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for the unified <b>users</b> table.
 * <p>
 * <ul>
 *   <li>{@code findByEmail} – look‑up during login / duplicate‑check</li>
 *   <li>{@code countByRole} – dashboard metrics (e.g. number of STUDENTs)</li>
 *   <li>{@code countByRoleAndActive} – count only active/inactive users of a specific role</li>
 *   <li>{@code findAllByRole} – list all users of a given role</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /* ───────────── Look‑ups ───────────── */

    Optional<User> findByEmail(String email);

    /* ───────────── Aggregates ─────────── */

    long countByRole(String role);

    long countByRoleAndActive(String role, boolean active); // ✅ New method

    /* ───────────── Collections ────────── */

    List<User> findAllByRole(String role);   // e.g. STUDENT or ADMIN
}
