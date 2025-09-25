package com.furkanerd.hr_management_system.service.performancereview.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.mapper.PerformanceReviewMapper;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.PerformanceReviewRepository;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PerformanceReviewManagementServiceImplTest {

    @InjectMocks
    private PerformanceReviewManagementServiceImpl service;

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PerformanceReviewMapper performanceReviewMapper;

    @Mock
    private NotificationService notificationService;

    private Employee employee;
    private Employee reviewer;
    private PerformanceReview performanceReview;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = Employee.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        reviewer = Employee.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();

        performanceReview = PerformanceReview.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .reviewer(reviewer)
                .rating(5)
                .comments("Good job")
                .reviewDate(LocalDate.now())
                .build();
    }

    // CREATE
    @Test
    void createPerformanceReview_success() {
        PerformanceReviewCreateRequest request = new PerformanceReviewCreateRequest(employee.getId(), 5, "Excellent", LocalDate.now());

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));
        when(performanceReviewRepository.save(any())).thenReturn(performanceReview);
        when(performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReview))
                .thenReturn(mock(PerformanceReviewDetailResponse.class));

        PerformanceReviewDetailResponse response = service.createPerformanceReview(request, reviewer.getEmail());
        assertNotNull(response);
        verify(notificationService).notify(eq(employee), anyString(), anyString(), eq(NotificationTypeEnum.PERFORMANCE));
    }

    @Test
    void createPerformanceReview_selfReview_throwsException() {
        PerformanceReviewCreateRequest request = new PerformanceReviewCreateRequest(reviewer.getId(), 5, "Excellent", LocalDate.now());

        when(employeeRepository.findById(reviewer.getId())).thenReturn(Optional.of(reviewer));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));

        assertThrows(SelfReviewNotAllowedException.class, () -> service.createPerformanceReview(request, reviewer.getEmail()));
    }

    @Test
    void createPerformanceReview_futureDate_throwsException() {
        PerformanceReviewCreateRequest request = new PerformanceReviewCreateRequest(employee.getId(), 5, "Excellent", LocalDate.now().plusDays(1));

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));

        assertThrows(InvalidReviewDateException.class, () -> service.createPerformanceReview(request, reviewer.getEmail()));
    }

    // UPDATE
    @Test
    void updatePerformanceReview_success() {
        PerformanceReviewUpdateRequest updateRequest = new PerformanceReviewUpdateRequest(4, "Updated comment", LocalDate.now());

        when(performanceReviewRepository.findById(performanceReview.getId())).thenReturn(Optional.of(performanceReview));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));
        when(performanceReviewRepository.save(any())).thenReturn(performanceReview);
        when(performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(any())).thenReturn(mock(PerformanceReviewDetailResponse.class));

        PerformanceReviewDetailResponse response = service.updatePerformanceReview(performanceReview.getId(), updateRequest, reviewer.getEmail());
        assertNotNull(response);
        assertEquals(4, performanceReview.getRating());
    }

    @Test
    void updatePerformanceReview_unauthorized_throwsException() {
        Employee otherReviewer = Employee.builder().id(UUID.randomUUID()).email("other@example.com").build();
        PerformanceReviewUpdateRequest updateRequest = new PerformanceReviewUpdateRequest(4, "Updated", LocalDate.now());

        when(performanceReviewRepository.findById(performanceReview.getId())).thenReturn(Optional.of(performanceReview));
        when(employeeRepository.findByEmail(otherReviewer.getEmail())).thenReturn(Optional.of(otherReviewer));

        assertThrows(UnauthorizedActionException.class, () ->
                service.updatePerformanceReview(performanceReview.getId(), updateRequest, otherReviewer.getEmail()));
    }

    @Test
    void updatePerformanceReview_futureDate_throwsException() {
        PerformanceReviewUpdateRequest updateRequest = new PerformanceReviewUpdateRequest(4, "Updated", LocalDate.now().plusDays(1));

        when(performanceReviewRepository.findById(performanceReview.getId())).thenReturn(Optional.of(performanceReview));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));

        assertThrows(InvalidReviewDateException.class, () ->
                service.updatePerformanceReview(performanceReview.getId(), updateRequest, reviewer.getEmail()));
    }

    @Test
    void updatePerformanceReview_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        PerformanceReviewUpdateRequest updateRequest = new PerformanceReviewUpdateRequest(4, "Updated", LocalDate.now());

        when(performanceReviewRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(PerformanceReviewNotFoundException.class, () ->
                service.updatePerformanceReview(id, updateRequest, reviewer.getEmail()));
    }

    // DELETE
    @Test
    void deletePerformanceReview_success() {
        when(performanceReviewRepository.findById(performanceReview.getId())).thenReturn(Optional.of(performanceReview));
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));

        service.deletePerformanceReview(performanceReview.getId(), reviewer.getEmail());
        verify(performanceReviewRepository).delete(performanceReview);
    }

    @Test
    void deletePerformanceReview_unauthorized_throwsException() {
        Employee otherReviewer = Employee.builder().id(UUID.randomUUID()).email("other@example.com").build();
        when(performanceReviewRepository.findById(performanceReview.getId())).thenReturn(Optional.of(performanceReview));
        when(employeeRepository.findByEmail(otherReviewer.getEmail())).thenReturn(Optional.of(otherReviewer));

        assertThrows(UnauthorizedActionException.class, () ->
                service.deletePerformanceReview(performanceReview.getId(), otherReviewer.getEmail()));
    }

    @Test
    void deletePerformanceReview_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(performanceReviewRepository.findById(id)).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail(reviewer.getEmail())).thenReturn(Optional.of(reviewer));

        assertThrows(PerformanceReviewNotFoundException.class, () ->
                service.deletePerformanceReview(id, reviewer.getEmail()));
    }
}
