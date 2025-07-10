package com.example.studentportal.repository;

import com.example.studentportal.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all active notifications visible to all users
    List<Notification> findByActiveTrueAndVisibleTo(String visibleTo);

    // Get all active notifications visible to all (general public)
    List<Notification> findByActiveTrueAndVisibleToOrVisibleTo(String specific, String all);
}
