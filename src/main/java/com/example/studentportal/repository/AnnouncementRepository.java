package com.example.studentportal.repository;

import com.example.studentportal.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // Optional: add custom queries if needed
}
