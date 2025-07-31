package com.example.studentportal.service;

import com.example.studentportal.model.Section;
import com.example.studentportal.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> findAllActiveSections() {
        return sectionRepository.findByActiveTrue();
    }

    public List<Section> findByCourseIdAndYearLevel(Long courseId, Integer yearLevel) {
        return sectionRepository.findByCourseIdAndYearLevel(courseId, yearLevel);
    }

    // Additional method if you need String input from controllers
    public List<Section> findByCourseIdAndYearLevelString(Long courseId, String yearLevelString) {
        try {
            Integer yearLevel = Integer.parseInt(yearLevelString);
            return sectionRepository.findByCourseIdAndYearLevel(courseId, yearLevel);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year level must be a valid number");
        }
    }

    public List<Section> findByCourseId(Long courseId) {
        return sectionRepository.findByCourseId(courseId);
    }

    public List<Section> findByYearLevel(Integer yearLevel) {
        return sectionRepository.findByYearLevel(yearLevel);
    }

    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    public Section save(Section section) {
        return sectionRepository.save(section);
    }

    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
    }

    public List<Section> searchByName(String name) {
        return sectionRepository.findByNameContainingIgnoreCase(name);
    }
}
