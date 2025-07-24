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

    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    public void save(Section section) {
        sectionRepository.save(section);
    }

    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
    }
}