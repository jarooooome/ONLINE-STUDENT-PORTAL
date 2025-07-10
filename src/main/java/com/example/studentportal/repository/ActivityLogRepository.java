package com.example.studentportal.repository;

import com.example.studentportal.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
    Page<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);
    long countByActionContaining(String action);
}