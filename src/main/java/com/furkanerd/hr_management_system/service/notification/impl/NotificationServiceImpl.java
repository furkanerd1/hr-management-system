package com.furkanerd.hr_management_system.service.notification.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.custom.NotificationException;
import com.furkanerd.hr_management_system.mapper.NotificationMapper;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.notification.NotificationResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Notification;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.NotificationRepository;
import com.furkanerd.hr_management_system.service.email.MailService;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final MailService mailService;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, EmployeeRepository employeeRepository, MailService mailService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
        this.mailService = mailService;
        this.notificationMapper = notificationMapper;
    }

    @Override
    @Transactional
    public void notify(Employee employee, String subject, String message, NotificationTypeEnum type) {
        Notification notification = Notification.builder()
                .employee(employee)
                .message(message)
                .type(type)
                .read(false)
                .build();

        notification = notificationRepository.save(notification);

        try {
            mailService.sendMail(employee.getEmail(), subject, message);
        } catch (Exception e) {
            log.error("Mail sending failed for notification {} to employee {}: {}", notification.getId(), employee.getEmail(), e.getMessage());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<NotificationResponse> getMyNotifications(String email, int page, int size, String sortBy, String sortDirection) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
        Pageable pageable = PaginationUtils.buildPageable(page, size, sortBy, sortDirection);

        Page<Notification> notificationPage = notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(employee.getId(), pageable);
        List<NotificationResponse> responseList = notificationMapper.notificationsToListNotificationResponse(notificationPage.getContent());

        return PaginatedResponse.of(responseList, notificationPage.getTotalElements(), page, size);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationException("Notification not found: " + notificationId));
        if (!notification.getEmployee().getId().equals(employee.getId())) {
            throw new NotificationException("You are not allowed to modify this notification");
        }
        notification.setRead(true);
        return notificationMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + email));
        List<Notification> notifications = notificationRepository.findByEmployeeIdAndReadFalse(employee.getId());
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);

    }

    @Override
    @Transactional
    public void deleteNotification(UUID notificationId, String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found: " + email));
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException("Notification not found: " + notificationId));
        if (!notification.getEmployee().getId().equals(employee.getId())) {
            throw new NotificationException("You are not allowed to delete this notification");
        }
        notificationRepository.delete(notification);
    }
}
