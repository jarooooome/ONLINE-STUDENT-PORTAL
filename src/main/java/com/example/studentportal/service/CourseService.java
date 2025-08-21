package com.example.studentportal.service;

import com.example.studentportal.model.Course;
import com.example.studentportal.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Save a new course
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Get all courses (both active and inactive)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Required for filtering in controller
    public List<Course> findAll() {
        return getAllCourses();
    }

    // Get only active courses (same as findAllActiveCourses)
    public List<Course> getActiveCourses() {
        return courseRepository.findByActiveTrue();
    }

    // Alias for getActiveCourses() to match controller usage
    public List<Course> findAllActiveCourses() {
        return courseRepository.findByActiveTrue();
    }

    // Find course by ID
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // Update course details
    public Course updateCourse(Long id, Course courseDetails) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setCode(courseDetails.getCode());
                    course.setTitle(courseDetails.getTitle());
                    course.setName(courseDetails.getName()); // Added name update
                    course.setDescription(courseDetails.getDescription());
                    course.setCreditHours(courseDetails.getCreditHours());
                    return courseRepository.save(course);
                })
                .orElseGet(() -> {
                    courseDetails.setId(id);
                    return courseRepository.save(courseDetails);
                });
    }

    // Deactivate course (soft delete)
    public void deactivateCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setActive(false);
            courseRepository.save(course);
        });
    }

    // Activate course
    public void activateCourse(Long id) {
        courseRepository.findById(id).ifPresent(course -> {
            course.setActive(true);
            courseRepository.save(course);
        });
    }

    // Count active courses
    public long countActiveCourses() {
        return courseRepository.countByActiveTrue();
    }

    // Count all courses (active + inactive)
    public long countAllCourses() {
        return courseRepository.count();
    }
}
