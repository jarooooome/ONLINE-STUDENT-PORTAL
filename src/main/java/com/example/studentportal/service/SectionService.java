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

    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    public List<Section> findAllActiveSections() {
        return sectionRepository.findByActiveTrue();
    }

    public List<Section> findByCourseIdAndYearLevel(Long courseId, Integer yearLevel) {
        if (courseId == null && yearLevel == null) {
            return findAll();
        }
        if (courseId == null) {
            return findByYearLevel(yearLevel);
        }
        if (yearLevel == null) {
            return findByCourseId(courseId);
        }
        return sectionRepository.findByCourseIdAndYearLevel(courseId, yearLevel);
    }

    public List<Section> findByCourseIdAndYearLevelString(Long courseId, String yearLevelString) {
        try {
            Integer yearLevel = yearLevelString != null && !yearLevelString.isEmpty() ?
                    Integer.parseInt(yearLevelString) : null;
            return findByCourseIdAndYearLevel(courseId, yearLevel);
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

    public List<Section> findByYearLevel(String yearLevelString) {
        try {
            Integer yearLevel = yearLevelString != null && !yearLevelString.isEmpty() ?
                    Integer.parseInt(yearLevelString) : null;
            return yearLevel != null ? findByYearLevel(yearLevel) : findAll();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Year level must be a valid number");
        }
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