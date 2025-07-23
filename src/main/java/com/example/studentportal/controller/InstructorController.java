package com.example.studentportal.controller;

import com.example.studentportal.model.Instructor;
import com.example.studentportal.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/instructors")
public class InstructorController {

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping
    public String listInstructors(Model model) {
        model.addAttribute("instructors", instructorRepository.findAll());
        return "instructors/instructors"; // list view
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("instructor", new Instructor());
        return "instructors/addinstructor"; // form view
    }

    @PostMapping("/add")
    public String saveInstructor(@ModelAttribute Instructor instructor) {
        instructorRepository.save(instructor);
        return "redirect:/admin/instructors";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid instructor ID: " + id));
        model.addAttribute("instructor", instructor);
        return "instructors/editinstructor";
    }

    @PostMapping("/edit/{id}")
    public String updateInstructor(@PathVariable Long id, @ModelAttribute Instructor instructorData) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid instructor ID: " + id));

        instructor.setFirstName(instructorData.getFirstName());
        instructor.setLastName(instructorData.getLastName());
        instructor.setEmail(instructorData.getEmail());
        instructor.setContactNumber(instructorData.getContactNumber());
        instructor.setDepartment(instructorData.getDepartment());
        instructor.setProfilePhoto(instructorData.getProfilePhoto());
        instructor.setActive(instructorData.isActive());

        instructorRepository.save(instructor);
        return "redirect:/admin/instructors";
    }

    @GetMapping("/delete/{id}")
    public String deleteInstructor(@PathVariable Long id) {
        instructorRepository.deleteById(id);
        return "redirect:/admin/instructors";
    }
}
