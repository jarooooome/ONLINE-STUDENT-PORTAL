package com.example.studentportal.service;

import com.example.studentportal.model.AcademicEvent;
import com.example.studentportal.repository.AcademicEventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AcademicEventService {

    private final AcademicEventRepository repository;

    public AcademicEventService(AcademicEventRepository repository) {
        this.repository = repository;
    }

    public List<AcademicEvent> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "date"));
    }

    public AcademicEvent findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public AcademicEvent save(AcademicEvent e) {
        return repository.save(e);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
