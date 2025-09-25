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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MailService mailService;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Captor
    private ArgumentCaptor<List<Notification>> notificationListCaptor;

    @Test
    @DisplayName("Should create notification and send email successfully")
    void notify_WhenValidInput_ShouldCreateNotificationAndSendEmail() {
        // given
        Employee employee = createTestEmployee();
        String subject = "Test Subject";
        String message = "Test Message";
        NotificationTypeEnum type = NotificationTypeEnum.PERFORMANCE;

        Notification savedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .message(message)
                .type(type)
                .read(false)
                .build();

        given(notificationRepository.save(any(Notification.class))).willReturn(savedNotification);

        // when
        notificationService.notify(employee, subject, message, type);

        // then
        then(notificationRepository).should().save(notificationCaptor.capture());
        Notification capturedNotification = notificationCaptor.getValue();
        assertThat(capturedNotification.getEmployee()).isEqualTo(employee);
        assertThat(capturedNotification.getMessage()).isEqualTo(message);
        assertThat(capturedNotification.getType()).isEqualTo(type);
        assertThat(capturedNotification.isRead()).isFalse();

        then(mailService).should().sendMail(employee.getEmail(), subject, message);
    }

    @Test
    @DisplayName("Should create notification even when email sending fails")
    void notify_WhenEmailSendingFails_ShouldStillCreateNotification() {
        // given
        Employee employee = createTestEmployee();
        String subject = "Test Subject";
        String message = "Test Message";
        NotificationTypeEnum type = NotificationTypeEnum.PERFORMANCE;

        Notification savedNotification = Notification.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .message(message)
                .type(type)
                .read(false)
                .build();

        given(notificationRepository.save(any(Notification.class))).willReturn(savedNotification);
        willThrow(new RuntimeException("Email service error")).given(mailService)
                .sendMail(employee.getEmail(), subject, message);

        // when
        notificationService.notify(employee, subject, message, type);

        // then
        then(notificationRepository).should().save(any(Notification.class));
        then(mailService).should().sendMail(employee.getEmail(), subject, message);
    }

    @Test
    @DisplayName("Should notify all employees for announcement")
    void notifyAllEmployeesForAnnouncement_ShouldCreateNotificationsForAllEmployees() {
        // given
        String subject = "Company Announcement";
        String message = "Important announcement message";
        List<Employee> employees = Arrays.asList(
                createTestEmployeeWithEmail("emp1@company.com"),
                createTestEmployeeWithEmail("emp2@company.com")
        );
        List<String> emails = Arrays.asList("emp1@company.com", "emp2@company.com");

        given(employeeRepository.findAll()).willReturn(employees);

        // when
        notificationService.notifyAllEmployeesForAnnouncement(subject, message);

        // then
        then(notificationRepository).should().saveAll(notificationListCaptor.capture());
        List<Notification> savedNotifications = notificationListCaptor.getValue();
        assertThat(savedNotifications).hasSize(2);
        assertThat(savedNotifications).allMatch(n ->
                n.getMessage().equals(message) &&
                        n.getType() == NotificationTypeEnum.ANNOUNCEMENT &&
                        !n.isRead()
        );

        then(mailService).should().sendBulkAnnouncementMail(emails, subject, message);
    }

    @Test
    @DisplayName("Should get paginated notifications for employee")
    void getMyNotifications_WhenValidEmail_ShouldReturnPaginatedNotifications() {
        // given
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        List<Notification> notifications = Arrays.asList(
                createTestNotification(employee),
                createTestNotification(employee)
        );
        Page<Notification> notificationPage = new PageImpl<>(notifications);
        List<NotificationResponse> responses = Arrays.asList(
                NotificationResponse.builder().id(UUID.randomUUID()).build(),
                NotificationResponse.builder().id(UUID.randomUUID()).build()
        );

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(eq(employee.getId()), any(Pageable.class)))
                .willReturn(notificationPage);
        given(notificationMapper.notificationsToListNotificationResponse(notifications)).willReturn(responses);

        // when
        PaginatedResponse<NotificationResponse> result = notificationService
                .getMyNotifications(email, 0, 10, "createdAt", "desc");

        // then
        assertThat(result.data()).hasSize(2);
        then(employeeRepository).should().findByEmail(email);
        then(notificationRepository).should()
                .findByEmployeeIdOrderByCreatedAtDesc(eq(employee.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found for getting notifications")
    void getMyNotifications_WhenEmployeeNotFound_ShouldThrowException() {
        // given
        String email = "nonexistent@company.com";
        given(employeeRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.getMyNotifications(email, 0, 10, "createdAt", "desc"))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().findByEmail(email);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void markAsRead_WhenValidRequest_ShouldMarkNotificationAsRead() {
        // given
        UUID notificationId = UUID.randomUUID();
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        Notification notification = createTestNotification(employee);
        notification.setRead(false);
        NotificationResponse expectedResponse = NotificationResponse.builder()
                .id(notificationId)
                .isRead(true)
                .build();

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));
        given(notificationRepository.save(notification)).willReturn(notification);
        given(notificationMapper.toNotificationResponse(notification)).willReturn(expectedResponse);

        // when
        NotificationResponse result = notificationService.markAsRead(notificationId, email);

        // then
        assertThat(notification.isRead()).isTrue();
        assertThat(result).isEqualTo(expectedResponse);
        then(notificationRepository).should().save(notification);
    }

    @Test
    @DisplayName("Should throw exception when trying to mark another employee's notification as read")
    void markAsRead_WhenNotificationBelongsToAnotherEmployee_ShouldThrowException() {
        // given
        UUID notificationId = UUID.randomUUID();
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        Employee anotherEmployee = createTestEmployeeWithEmail("another@company.com");
        Notification notification = createTestNotification(anotherEmployee);

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(notificationId, email))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("You are not allowed to modify this notification");
    }

    @Test
    @DisplayName("Should mark all notifications as read for employee")
    void markAllAsRead_WhenValidEmail_ShouldMarkAllNotificationsAsRead() {
        // given
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        List<Notification> notifications = Arrays.asList(
                createTestNotification(employee),
                createTestNotification(employee)
        );
        notifications.forEach(n -> n.setRead(false));

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findByEmployeeIdAndReadFalse(employee.getId())).willReturn(notifications);

        // when
        notificationService.markAllAsRead(email);

        // then
        assertThat(notifications).allMatch(Notification::isRead);
        then(notificationRepository).should().saveAll(notifications);
    }

    @Test
    @DisplayName("Should delete notification when user is owner")
    void deleteNotification_WhenUserIsOwner_ShouldDeleteNotification() {
        // given
        UUID notificationId = UUID.randomUUID();
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        Notification notification = createTestNotification(employee);

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when
        notificationService.deleteNotification(notificationId, email);

        // then
        then(notificationRepository).should().delete(notification);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete another employee's notification")
    void deleteNotification_WhenNotificationBelongsToAnotherEmployee_ShouldThrowException() {
        // given
        UUID notificationId = UUID.randomUUID();
        String email = "john.doe@company.com";
        Employee employee = createTestEmployeeWithEmail(email);
        Employee anotherEmployee = createTestEmployeeWithEmail("another@company.com");
        Notification notification = createTestNotification(anotherEmployee);

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

        // when & then
        assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, email))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("You are not allowed to delete this notification");
    }

    private Employee createTestEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID())
                .email("john.doe@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    private Employee createTestEmployeeWithEmail(String email) {
        return Employee.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    private Notification createTestNotification(Employee employee) {
        return Notification.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .message("Test message")
                .type(NotificationTypeEnum.PERFORMANCE)
                .read(false)
                .build();
    }
}