package com.example.studentportal.controller;

import com.example.studentportal.model.Subject;
import com.example.studentportal.model.Course;
import com.example.studentportal.repository.SubjectRepository;
import com.example.studentportal.repository.CourseRepository;
import com.example.studentportal.dto.SubjectDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Show all subjects
    @GetMapping("/subjects")
    public String viewSubjects(Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        return "admin/subjects";
    }

    // Show form to add a new subject
    @GetMapping("/subjects/add")
    public String showAddSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("existingSubjects", subjectRepository.findAll());
        return "admin/addsubject";
    }

    // Save the new subject
    @PostMapping("/subjects/save")
    public String saveSubject(@ModelAttribute("subject") Subject subject, RedirectAttributes redirectAttributes) {
        // Check for duplicate subject code
        if (subjectRepository.existsByCodeIgnoreCase(subject.getCode())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject code already exists");
            return "redirect:/admin/subjects/add";
        }

        // Check for duplicate subject name
        if (subjectRepository.existsByNameIgnoreCase(subject.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject name already exists");
            return "redirect:/admin/subjects/add";
        }

        subjectRepository.save(subject);
        return "redirect:/admin/subjects";
    }

    // Show form to edit a subject
    @GetMapping("/subjects/edit/{id}")
    public String showEditSubjectForm(@PathVariable("id") Long id, Model model) {
        Subject subject = subjectRepository.findById(id).orElse(null);
        if (subject == null) return "redirect:/admin/subjects";

        model.addAttribute("subject", subject);
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("existingSubjects", subjectRepository.findAll());
        return "admin/editsubject";
    }

    // Save updated subject
    @PostMapping("/subjects/update")
    public String updateSubject(@ModelAttribute("subject") Subject subject) {
        subjectRepository.save(subject);
        return "redirect:/admin/subjects";
    }

    // Delete subject
    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable("id") Long id) {
        subjectRepository.deleteById(id);
        return "redirect:/admin/subjects";
    }

    // Load subjects by courseId using DTOs
    @GetMapping("/subjects/by-course")
    @ResponseBody
    public List<SubjectDTO> getSubjectsByCourse(@RequestParam Long courseId) {
        List<Subject> subjects = subjectRepository.findByCourseId(courseId);
        if (subjects == null) return List.of();

        return subjects.stream()
                .map(s -> new SubjectDTO(
                        s.getId(),
                        s.getCode(),
                        s.getName(),
                        s.getSemester()
                ))
                .collect(Collectors.toList());
    }
}
