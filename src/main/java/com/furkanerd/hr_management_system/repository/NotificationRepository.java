package com.furkanerd.hr_management_system.repository;

import com.furkanerd.hr_management_system.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId, Pageable pageable);

    List<Notification> findByEmployeeIdAndReadFalse(UUID employeeId);
}
