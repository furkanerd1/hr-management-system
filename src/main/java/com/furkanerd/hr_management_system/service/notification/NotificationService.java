package com.furkanerd.hr_management_system.service.notification;

import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.notification.NotificationResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;

import java.util.UUID;

public interface NotificationService {
    void notify(Employee employee, String subject, String message, NotificationTypeEnum type);
    PaginatedResponse<NotificationResponse> getMyNotifications(String email, int page, int size, String sortBy, String sortDirection);
    NotificationResponse markAsRead(UUID notificationId, String email);
    void markAllAsRead(String email);
    void deleteNotification(UUID notificationId, String email);
}
