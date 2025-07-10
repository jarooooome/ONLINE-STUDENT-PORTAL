package com.example.studentportal.service;

import com.example.studentportal.model.Notification;
import com.example.studentportal.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all active notifications visible to a specific role (e.g., STUDENT or ADMIN)
    public List<Notification> getNotificationsForRole(String role) {
        return notificationRepository.findByActiveTrueAndVisibleToOrVisibleTo(role, "ALL");
    }

    // Admin: Create a new notification
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    // Admin: Deactivate (soft delete) a notification
    public void deactivateNotification(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setActive(false);
            notificationRepository.save(notification);
        });
    }

    // Admin: Reactivate a notification
    public void activateNotification(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setActive(true);
            notificationRepository.save(notification);
        });
    }

    // Admin: Update notification
    public Notification updateNotification(Long id, Notification updated) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setMessage(updated.getMessage());
                    existing.setVisibleTo(updated.getVisibleTo());
                    return notificationRepository.save(existing);
                })
                .orElseGet(() -> {
                    updated.setId(id);
                    return notificationRepository.save(updated);
                });
    }

    // Admin: Get all notifications (active and inactive)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
