package com.example.studentportal.controller;

import com.example.studentportal.model.Subject;
import com.example.studentportal.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public String getAllSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "admin/subjects";
    }

    @GetMapping("/add")
    public String showAddSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "admin/addsubject";
    }

    @PostMapping("/add")
    public String addSubject(@ModelAttribute Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/admin/subjects";
    }

    @GetMapping("/edit/{id}")
    public String showEditSubjectForm(@PathVariable Long id, Model model) {
        Subject subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        return "admin/editsubject";
    }

    @PostMapping("/edit/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute Subject subject) {
        subjectService.updateSubject(id, subject);
        return "redirect:/admin/subjects";
    }

    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/admin/subjects";
    }
}
