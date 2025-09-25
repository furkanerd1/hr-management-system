package com.furkanerd.hr_management_system.service.attendance.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceManagementServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceManagementServiceImpl attendanceService;

    private Employee employee;
    private Attendance attendance;
    private AttendanceDetailResponse attendanceResponse;
    private AttendanceCreateRequest createRequest;

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

        attendanceResponse = AttendanceDetailResponse.builder()
                .id(attendance.getId())
                .employeeId(employee.getId())
                .date(attendance.getDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .build();

        createRequest = new AttendanceCreateRequest(
                employee.getId(),
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );
    }

    @Test
    void createAttendance_WithValidData_ShouldCreateAttendanceSuccessfully() {
        // Given
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), createRequest.date())).thenReturn(false);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
        when(attendanceMapper.attendanceToAttendanceDetailResponse(attendance)).thenReturn(attendanceResponse);

        // When
        AttendanceDetailResponse result = attendanceService.createAttendance(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(attendanceResponse.id(), result.id());
        assertEquals(attendanceResponse.employeeId(), result.employeeId());

        // Verify
        verify(employeeRepository).findById(employee.getId());
        verify(attendanceRepository).existsByEmployeeIdAndDate(employee.getId(), createRequest.date());
        verify(attendanceRepository).save(any(Attendance.class));
        verify(attendanceMapper).attendanceToAttendanceDetailResponse(attendance);
    }

    @Test
    void createAttendance_WithInvalidEmployee_ShouldThrowEmployeeNotFoundException() {
        // Given
        UUID invalidEmployeeId = UUID.randomUUID();
        AttendanceCreateRequest invalidRequest = new AttendanceCreateRequest(
                invalidEmployeeId, LocalDate.now(), LocalTime.of(8, 0), null
        );
        when(employeeRepository.findById(invalidEmployeeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EmployeeNotFoundException.class, () ->
                attendanceService.createAttendance(invalidRequest));

        // Verify
        verify(employeeRepository).findById(invalidEmployeeId);
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void createAttendance_WithExistingAttendance_ShouldThrowAttendanceAlreadyExistsException() {
        // Given
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), createRequest.date())).thenReturn(true);

        // When & Then
        assertThrows(AttendanceAlreadyExistsException.class, () ->
                attendanceService.createAttendance(createRequest));

        // Verify
        verify(employeeRepository).findById(employee.getId());
        verify(attendanceRepository).existsByEmployeeIdAndDate(employee.getId(), createRequest.date());
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void createAttendance_WithInvalidCheckInTime_ShouldThrowInvalidAttendanceTimeException() {
        // Given
        AttendanceCreateRequest invalidTimeRequest = new AttendanceCreateRequest(
                employee.getId(), LocalDate.now(), LocalTime.of(5, 0), null // Too early
        );
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), invalidTimeRequest.date())).thenReturn(false);

        // When & Then
        assertThrows(InvalidAttendanceTimeException.class, () ->
                attendanceService.createAttendance(invalidTimeRequest));

        // Verify
        verify(employeeRepository).findById(employee.getId());
        verify(attendanceRepository).existsByEmployeeIdAndDate(employee.getId(), invalidTimeRequest.date());
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void autoCheckIn_WithValidTime_ShouldCreateAttendanceSuccessfully() {
        // Given
        try (MockedStatic<LocalDate> localDateMock = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class)) {

            LocalDate today = LocalDate.of(2023, 10, 15);
            LocalTime validTime = LocalTime.of(8, 0);

            localDateMock.when(LocalDate::now).thenReturn(today);
            localTimeMock.when(LocalTime::now).thenReturn(validTime);

            when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
            when(attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), today)).thenReturn(false);
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
            when(attendanceMapper.attendanceToAttendanceDetailResponse(attendance)).thenReturn(attendanceResponse);

            // When
            AttendanceDetailResponse result = attendanceService.autoCheckIn(employee.getEmail());

            // Then
            assertNotNull(result);
            assertEquals(attendanceResponse.id(), result.id());

            // Verify
            verify(employeeRepository).findByEmail(employee.getEmail());
            verify(attendanceRepository).existsByEmployeeIdAndDate(employee.getId(), today);
            verify(attendanceRepository).save(any(Attendance.class));
        }
    }

    @Test
    void autoCheckIn_WithInvalidTime_ShouldThrowInvalidAttendanceTimeException() {
        // Given
        try (MockedStatic<LocalDate> localDateMock = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class)) {

            LocalDate today = LocalDate.of(2023, 10, 15);
            LocalTime invalidTime = LocalTime.of(5, 0); // Too early

            localDateMock.when(LocalDate::now).thenReturn(today);
            localTimeMock.when(LocalTime::now).thenReturn(invalidTime);

            when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

            // When & Then
            assertThrows(InvalidAttendanceTimeException.class, () ->
                    attendanceService.autoCheckIn(employee.getEmail()));

            // Verify
            verify(employeeRepository).findByEmail(employee.getEmail());
            verify(attendanceRepository, never()).save(any());
        }
    }

    @Test
    void autoCheckOut_WithValidAttendance_ShouldUpdateAttendanceSuccessfully() {
        // Given
        try (MockedStatic<LocalDate> localDateMock = Mockito.mockStatic(LocalDate.class);
             MockedStatic<LocalTime> localTimeMock = Mockito.mockStatic(LocalTime.class)) {

            LocalDate today = LocalDate.of(2023, 10, 15);
            LocalTime checkOutTime = LocalTime.of(17, 0);

            localDateMock.when(LocalDate::now).thenReturn(today);
            localTimeMock.when(LocalTime::now).thenReturn(checkOutTime);

            Attendance attendanceWithoutCheckOut = Attendance.builder()
                    .id(UUID.randomUUID())
                    .employee(employee)
                    .date(today)
                    .checkInTime(LocalTime.of(8, 0))
                    .build();

            when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
            when(attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today))
                    .thenReturn(Optional.of(attendanceWithoutCheckOut));
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
            when(attendanceMapper.attendanceToAttendanceDetailResponse(attendance)).thenReturn(attendanceResponse);

            // When
            AttendanceDetailResponse result = attendanceService.autoCheckOut(employee.getEmail());

            // Then
            assertNotNull(result);
            assertEquals(attendanceResponse.id(), result.id());

            // Verify
            verify(employeeRepository).findByEmail(employee.getEmail());
            verify(attendanceRepository).findByEmployeeIdAndDate(employee.getId(), today);
            verify(attendanceRepository).save(attendanceWithoutCheckOut);
        }
    }

    @Test
    void autoCheckOut_WithAlreadyCheckedOut_ShouldThrowAlreadyCheckedOutException() {
        // Given
        try (MockedStatic<LocalDate> localDateMock = Mockito.mockStatic(LocalDate.class)) {
            LocalDate today = LocalDate.of(2023, 10, 15);
            localDateMock.when(LocalDate::now).thenReturn(today);

            Attendance alreadyCheckedOut = Attendance.builder()
                    .employee(employee)
                    .checkInTime(LocalTime.of(8, 0))
                    .checkOutTime(LocalTime.of(17, 0)) // Already checked out
                    .build();

            when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
            when(attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today))
                    .thenReturn(Optional.of(alreadyCheckedOut));

            // When & Then
            assertThrows(AlreadyCheckedOutException.class, () ->
                    attendanceService.autoCheckOut(employee.getEmail()));

            // Verify
            verify(employeeRepository).findByEmail(employee.getEmail());
            verify(attendanceRepository).findByEmployeeIdAndDate(employee.getId(), today);
            verify(attendanceRepository, never()).save(any());
        }
    }

    @Test
    void updateAttendance_WithValidData_ShouldUpdateSuccessfully() {
        // Given
        UUID attendanceId = attendance.getId();
        AttendanceUpdateRequest updateRequest = new AttendanceUpdateRequest(
                LocalTime.of(8, 30), LocalTime.of(17, 30)
        );

        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);
        when(attendanceMapper.attendanceToAttendanceDetailResponse(attendance)).thenReturn(attendanceResponse);

        // When
        AttendanceDetailResponse result = attendanceService.updateAttendance(attendanceId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(attendanceResponse.id(), result.id());

        // Verify
        verify(attendanceRepository).findById(attendanceId);
        verify(attendanceRepository).save(attendance);
        verify(attendanceMapper).attendanceToAttendanceDetailResponse(attendance);
    }

    @Test
    void updateAttendance_WithInvalidId_ShouldThrowAttendanceNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        AttendanceUpdateRequest updateRequest = new AttendanceUpdateRequest(
                LocalTime.of(8, 30), LocalTime.of(17, 30)
        );

        when(attendanceRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendanceNotFoundException.class, () ->
                attendanceService.updateAttendance(invalidId, updateRequest));

        // Verify
        verify(attendanceRepository).findById(invalidId);
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    void deleteAttendance_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        UUID attendanceId = attendance.getId();
        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendance));

        // When
        assertDoesNotThrow(() -> attendanceService.deleteAttendance(attendanceId));

        // Then & Verify
        verify(attendanceRepository).findById(attendanceId);
        verify(attendanceRepository).delete(attendance);
    }

    @Test
    void deleteAttendance_WithInvalidId_ShouldThrowAttendanceNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(attendanceRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AttendanceNotFoundException.class, () ->
                attendanceService.deleteAttendance(invalidId));

        // Verify
        verify(attendanceRepository).findById(invalidId);
        verify(attendanceRepository, never()).delete((Attendance) any());
    }
}