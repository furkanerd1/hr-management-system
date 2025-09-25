package com.furkanerd.hr_management_system.service.attendance.impl;

import com.furkanerd.hr_management_system.exception.custom.AttendanceNotFoundException;
import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceQueryServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceQueryServiceImpl attendanceQueryService;

    private Employee employee;
    private Attendance attendance;
    private AttendanceDetailResponse attendanceDetailResponse;
    private ListAttendanceResponse listAttendanceResponse;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(UUID.randomUUID())
                .email("employee@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        attendance = Attendance.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .date(LocalDate.now())
                .checkInTime(LocalTime.of(8, 0))
                .checkOutTime(LocalTime.of(17, 0))
                .build();

        attendanceDetailResponse = AttendanceDetailResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .build();

        listAttendanceResponse = ListAttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .build();
    }

    @Test
    void listAllAttendance_WithValidParameters_ShouldReturnPaginatedResponse() {
        // Given
        AttendanceFilterRequest filterRequest = AttendanceFilterRequest.empty();
        List<Attendance> attendances = Arrays.asList(attendance);
        Page<Attendance> attendancePage = new PageImpl<>(attendances, Pageable.unpaged(), attendances.size());
        List<ListAttendanceResponse> responses = Arrays.asList(listAttendanceResponse);

        when(attendanceRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(attendancePage);
        when(attendanceMapper.attendancesToListAttendanceResponse(attendances)).thenReturn(responses);

        // When
        PaginatedResponse<ListAttendanceResponse> result = attendanceQueryService.listAllAttendance(
                0, 10, "date", "desc", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals(listAttendanceResponse.id(), result.data().getFirst().id());

        // Verify
        verify(attendanceRepository).findAll(nullable(Specification.class), any(Pageable.class));
        verify(attendanceMapper).attendancesToListAttendanceResponse(attendances);
    }

    @Test
    void getAttendanceById_WithValidId_ShouldReturnAttendanceDetailResponse() {
        // Given
        UUID attendanceId = attendance.getId();
        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendance));
        when(attendanceMapper.attendanceToAttendanceDetailResponse(attendance)).thenReturn(attendanceDetailResponse);

        // When
        AttendanceDetailResponse result = attendanceQueryService.getAttendanceById(attendanceId);

        // Then
        assertNotNull(result);
        assertEquals(attendanceDetailResponse.id(), result.id());
        assertEquals(attendanceDetailResponse.employeeId(), result.employeeId());

        // Verify
        verify(attendanceRepository).findById(attendanceId);
        verify(attendanceMapper).attendanceToAttendanceDetailResponse(attendance);
    }

    @Test
    void getAttendanceById_WithInvalidId_ShouldThrowAttendanceNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(attendanceRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendanceNotFoundException.class, () ->
                attendanceQueryService.getAttendanceById(invalidId));

        // Verify
        verify(attendanceRepository).findById(invalidId);
        verify(attendanceMapper, never()).attendanceToAttendanceDetailResponse(any());
    }

    @Test
    void getAttendanceByEmployee_WithValidEmail_ShouldReturnPaginatedResponse() {
        // Given
        String employeeEmail = employee.getEmail();
        AttendanceFilterRequest filterRequest = AttendanceFilterRequest.empty();
        List<Attendance> attendances = Arrays.asList(attendance);
        Page<Attendance> attendancePage = new PageImpl<>(attendances, Pageable.unpaged(), attendances.size());
        List<ListAttendanceResponse> responses = Arrays.asList(listAttendanceResponse);

        when(employeeRepository.findByEmail(employeeEmail)).thenReturn(Optional.of(employee));
        when(attendanceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(attendancePage);
        when(attendanceMapper.attendancesToListAttendanceResponse(attendances)).thenReturn(responses);

        // When
        PaginatedResponse<ListAttendanceResponse> result = attendanceQueryService.getAttendanceByEmployee(
                employeeEmail, 0, 10, "date", "desc", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());

        // Verify
        verify(employeeRepository).findByEmail(employeeEmail);
        verify(attendanceRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(attendanceMapper).attendancesToListAttendanceResponse(attendances);
    }

    @Test
    void getAttendanceByEmployee_WithInvalidEmail_ShouldThrowEmployeeNotFoundException() {
        // Given
        String invalidEmail = "invalid@company.com";
        AttendanceFilterRequest filterRequest = AttendanceFilterRequest.empty();
        when(employeeRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EmployeeNotFoundException.class, () ->
                attendanceQueryService.getAttendanceByEmployee(invalidEmail, 0, 10, "date", "desc", filterRequest));

        // Verify
        verify(employeeRepository).findByEmail(invalidEmail);
        verify(attendanceRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}