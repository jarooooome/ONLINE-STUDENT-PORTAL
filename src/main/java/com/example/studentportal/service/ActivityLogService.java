package com.example.studentportal.service;

import com.example.studentportal.model.ActivityLog;
import com.example.studentportal.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public ActivityLog logActivity(String action, String performedBy,
                                   String targetEntity, Long targetId,
                                   String details) {
        ActivityLog log = new ActivityLog(action, performedBy,
                targetEntity, targetId, details);
        return activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop10ByOrderByTimestampDesc();
    }

    public long countPendingActions() {
        return activityLogRepository.countByActionContaining("PENDING");
    }
}