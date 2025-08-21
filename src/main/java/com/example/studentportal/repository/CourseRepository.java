package com.example.studentportal.repository;

import com.example.studentportal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Basic CRUD operations are inherited from JpaRepository

    /**
     * Finds all active courses
     * @return List of active courses
     */
    List<Course> findByActiveTrue();
    List<Course> findByActiveFalse();

    /**
     * Finds course by course code (e.g. "BSIT")
     * @param code The course code
     * @return Optional containing the course if found
     */
    Optional<Course> findByCode(String code);

    /**
     * Counts all active courses
     * @return Number of active courses
     */
    long countByActiveTrue();


    /**
     * Finds courses containing search term in name or code (case-insensitive)
     * @param searchTerm Term to search for
     * @return List of matching courses
     */
    List<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String searchTerm, String searchTerm2);

    /**
     * Checks if a course with given code exists (for validation)
     * @param code Course code to check
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Finds courses by active status and name containing (for admin filtering)
     * @param active Active status
     * @param name Name search term
     * @return List of matching courses
     */
    List<Course> findByActiveAndNameContainingIgnoreCase(boolean active, String name);
    long count();
}