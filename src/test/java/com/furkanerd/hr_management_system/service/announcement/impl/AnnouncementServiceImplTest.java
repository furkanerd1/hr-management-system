package com.furkanerd.hr_management_system.service.announcement.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.AnnouncementMapper;
import com.furkanerd.hr_management_system.model.dto.request.announcement.AnnouncementCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.announcement.AnnouncementResponse;
import com.furkanerd.hr_management_system.model.entity.Announcement;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.enums.AnnouncementType;
import com.furkanerd.hr_management_system.repository.AnnouncementRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceImplTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AnnouncementMapper announcementMapper;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    private Employee hrEmployee;
    private AnnouncementCreateRequest createRequest;
    private Announcement announcement;
    private AnnouncementResponse announcementResponse;

    @BeforeEach
    void setUp() {
        // Given
        hrEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("hr@company.com")
                .firstName("HR")
                .lastName("Manager")
                .build();

        createRequest = new AnnouncementCreateRequest(
                "Important Announcement",
                "This is an important announcement",
                AnnouncementType.GENERAL
        );

        announcement = Announcement.builder()
                .id(UUID.randomUUID())
                .title(createRequest.title())
                .content(createRequest.content())
                .type(createRequest.type())
                .createdBy(hrEmployee)
                .build();

        announcementResponse = AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType())
                .build();
    }

    @Test
    void createAnnouncement_WithValidData_ShouldCreateAnnouncementSuccessfully() {
        // Given
        when(employeeRepository.findByEmail(hrEmployee.getEmail())).thenReturn(Optional.of(hrEmployee));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);
        when(announcementMapper.toAnnouncementResponse(announcement)).thenReturn(announcementResponse);

        // When
        AnnouncementResponse result = announcementService.createAnnouncement(createRequest, hrEmployee.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(announcementResponse.id(), result.id());
        assertEquals(announcementResponse.title(), result.title());
        assertEquals(announcementResponse.content(), result.content());

        // Verify
        verify(employeeRepository).findByEmail(hrEmployee.getEmail());
        verify(announcementRepository).save(any(Announcement.class));
        verify(notificationService).notifyAllEmployeesForAnnouncement(anyString(), anyString());
        verify(announcementMapper).toAnnouncementResponse(announcement);
    }

    @Test
    void createAnnouncement_WithInvalidHrEmail_ShouldThrowEmployeeNotFoundException() {
        // Given
        String invalidEmail = "invalid@company.com";
        when(employeeRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EmployeeNotFoundException.class, () ->
                announcementService.createAnnouncement(createRequest, invalidEmail));

        // Verify
        verify(employeeRepository).findByEmail(invalidEmail);
        verify(announcementRepository, never()).save(any());
        verify(notificationService, never()).notifyAllEmployeesForAnnouncement(anyString(), anyString());
    }

    @Test
    void getAllAnnouncements_WithValidPagination_ShouldReturnPaginatedResponse() {
        // Given
        List<Announcement> announcements = Arrays.asList(announcement);
        Page<Announcement> announcementPage = new PageImpl<>(announcements, Pageable.unpaged(), announcements.size());
        List<AnnouncementResponse> responses = Arrays.asList(announcementResponse);

        when(announcementRepository.findAll(any(Pageable.class))).thenReturn(announcementPage);
        when(announcementMapper.toAnnouncementResponseList(announcements)).thenReturn(responses);

        // When
        PaginatedResponse<AnnouncementResponse> result = announcementService.getAllAnnouncements(0, 10, "id", "asc");

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals(announcementResponse.id(), result.data().getFirst().id());

        // Verify
        verify(announcementRepository).findAll(any(Pageable.class));
        verify(announcementMapper).toAnnouncementResponseList(announcements);
    }
}
