package com.furkanerd.hr_management_system.service.leaverequest.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.model.enums.LeaveTypeEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveRequestManagementServiceImplTest {

    @InjectMocks
    private LeaveRequestManagementServiceImpl service;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @Mock
    private NotificationService notificationService;

    private Employee employee;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = Employee.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .vacationBalance(10)
                .maternityBalance(5)
                .build();

        leaveRequest = LeaveRequest.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .leaveType(LeaveTypeEnum.VACATION)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .status(LeaveStatusEnum.PENDING)
                .build();
    }

    // CREATE
    @Test
    void createLeaveRequest_success() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest(
                LeaveTypeEnum.VACATION, LocalDate.now(), LocalDate.now().plusDays(2), "Vacation"
        );
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequest))
                .thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.createLeaveRequest(request, employee.getEmail());

        assertNotNull(response);
        verify(notificationService).notify(eq(employee), anyString(), anyString(), any());
    }

    @Test
    void createLeaveRequest_insufficientBalance_throwsException() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest(
                LeaveTypeEnum.VACATION, LocalDate.now(), LocalDate.now().plusDays(20), "Vacation"
        );
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        assertThrows(InsufficientLeaveBalanceException.class,
                () -> service.createLeaveRequest(request, employee.getEmail()));
    }

    // EDIT
    @Test
    void editLeaveRequest_success() {
        LeaveRequestEditRequest editRequest = new LeaveRequestEditRequest(
                LocalDate.now(), LocalDate.now().plusDays(1), LeaveTypeEnum.VACATION, "Edited reason"
        );
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(any())).thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.editLeaveRequest(leaveRequest.getId(), editRequest, employee.getEmail());

        assertNotNull(response);
        assertEquals(editRequest.leaveType(), leaveRequest.getLeaveType());
    }

    @Test
    void editLeaveRequest_wrongEmployee_throwsException() {
        LeaveRequestEditRequest editRequest = new LeaveRequestEditRequest(
                LocalDate.now(), LocalDate.now().plusDays(1), LeaveTypeEnum.VACATION, "Edited reason"
        );
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));

        assertThrows(UnauthorizedActionException.class,
                () -> service.editLeaveRequest(leaveRequest.getId(), editRequest, "someone@example.com"));
    }

    @Test
    void editLeaveRequest_alreadyProcessed_throwsException() {
        leaveRequest.setStatus(LeaveStatusEnum.APPROVED);
        LeaveRequestEditRequest editRequest = new LeaveRequestEditRequest(
                LocalDate.now(), LocalDate.now().plusDays(1), LeaveTypeEnum.VACATION, "Edited reason"
        );
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));

        assertThrows(LeaveRequestAlreadyProcessedException.class,
                () -> service.editLeaveRequest(leaveRequest.getId(), editRequest, employee.getEmail()));
    }

    // APPROVE
    @Test
    void approveLeaveRequest_success() {
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(any())).thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.approveLeaveRequest(leaveRequest.getId(), employee.getEmail());

        assertNotNull(response);
        assertEquals(LeaveStatusEnum.APPROVED, leaveRequest.getStatus());
    }

    @Test
    void approveLeaveRequest_insufficientVacationBalance_rejects() {
        leaveRequest.setEndDate(leaveRequest.getStartDate().plusDays(20)); // more than balance
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(any())).thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.approveLeaveRequest(leaveRequest.getId(), employee.getEmail());

        assertEquals(LeaveStatusEnum.REJECTED, leaveRequest.getStatus());
    }

    // REJECT
    @Test
    void rejectLeaveRequest_success() {
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(any())).thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.rejectLeaveRequest(leaveRequest.getId(), employee.getEmail());

        assertEquals(LeaveStatusEnum.REJECTED, leaveRequest.getStatus());
    }

    // CANCEL
    @Test
    void cancelLeaveRequest_success() {
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));

        service.cancelLeaveRequest(leaveRequest.getId(), employee.getEmail());

        verify(leaveRequestRepository).delete(leaveRequest);
        verify(notificationService).notify(eq(employee), anyString(), anyString(), any());
    }

    @Test
    void cancelLeaveRequest_wrongEmployee_throwsException() {
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));

        assertThrows(UnauthorizedActionException.class,
                () -> service.cancelLeaveRequest(leaveRequest.getId(), "someone@example.com"));
    }

    @Test
    void cancelLeaveRequest_notPending_throwsException() {
        leaveRequest.setStatus(LeaveStatusEnum.APPROVED);
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));

        assertThrows(UnauthorizedActionException.class,
                () -> service.cancelLeaveRequest(leaveRequest.getId(), employee.getEmail()));
    }
}