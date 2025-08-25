package com.example.studentportal.controller;

import com.example.studentportal.model.User;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.CourseService;
import com.example.studentportal.service.SectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/registrar/students")
public class RegistrarStudentController {

    private final UserService userService;
    private final CourseService courseService;
    private final SectionService sectionService;

    public RegistrarStudentController(UserService userService,
                                      CourseService courseService,
                                      SectionService sectionService) {
        this.userService = userService;
        this.courseService = courseService;
        this.sectionService = sectionService;
    }

    /* ─────────────── LIST: /registrar/students ─────────────── */
    @GetMapping
    public String listStudents(@RequestParam(required = false) Long courseId,
                               @RequestParam(required = false) Integer yearLevel,
                               @RequestParam(required = false) Long sectionId,
                               Model model,
                               Principal principal) {

        List<User> students = userService.filterStudents(courseId, yearLevel, sectionId);
        model.addAttribute("students", students);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("currentRegistrar", principal.getName());
        return "registrar/students";
    }

    /* ─────────────── VIEW: /registrar/students/{id} ────────── */
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        model.addAttribute("student", student);
        return "registrar/student-view";
    }

    /* ─────────────── FORM: /registrar/students/new ─────────── */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User student = new User();
        student.setRole("STUDENT");
        model.addAttribute("student", student);
        model.addAttribute("courses", courseService.findAllActiveCourses());
        return "registrar/student-create";
    }

    /* ─────────────── CREATE (POST) ─────────────────────── */
    @PostMapping
    public String createStudent(@ModelAttribute("student") User student) {
        student.setRole("STUDENT");
        userService.saveUser(student);
        return "redirect:/registrar/students";
    }

    /* ─────────────── EDIT FORM: /registrar/students/{id}/edit ─ */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User student = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        model.addAttribute("student", student);
        model.addAttribute("courses", courseService.findAllActiveCourses());
        return "registrar/student-edit";
    }

    /* ─────────────── UPDATE (POST) ─────────────────────── */
    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") User student) {
        User existingStudent = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));

        // Update the existing student with new values
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setStudentId(student.getStudentId());
        existingStudent.setCourse(student.getCourse());
        existingStudent.setYearLevel(student.getYearLevel());
        existingStudent.setSection(student.getSection());
        existingStudent.setActive(student.isActive());

        userService.updateUser(existingStudent);
        return "redirect:/registrar/students";
    }

    /* ─────────────── DEACTIVATE ────────────────────────── */
    @PostMapping("/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id) {
        User u = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        u.setActive(false);
        userService.updateUser(u);
        return "redirect:/registrar/students";
    }

    /* ─────────────── ACTIVATE ──────────────────────────── */
    @PostMapping("/{id}/activate")
    public String activateStudent(@PathVariable Long id) {
        User u = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));
        u.setActive(true);
        userService.updateUser(u);
        return "redirect:/registrar/students";
    }

    /* ─────────────── DELETE ────────────────────────────── */
    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        // In a real application, you would call a service method to delete the user
        // For now, we'll just redirect back to the student list
        // userService.deleteUser(id); // Uncomment when you implement this method
        return "redirect:/registrar/students";
    }
}