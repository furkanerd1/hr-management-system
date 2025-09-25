package com.furkanerd.hr_management_system.service.leaverequest.impl;

import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaveRequestQueryServiceImplTest {

    @InjectMocks
    private LeaveRequestQueryServiceImpl service;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    private LeaveRequest leaveRequest;
    private Employee employee;

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
                .build();
    }

    @Test
    void getLeaveRequestById_success() {
        when(leaveRequestRepository.findById(leaveRequest.getId())).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequest))
                .thenReturn(mock(LeaveRequestDetailResponse.class));

        LeaveRequestDetailResponse response = service.getLeaveRequestById(leaveRequest.getId());
        assertNotNull(response);
    }

    @Test
    void getLeaveRequestById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(leaveRequestRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> service.getLeaveRequestById(id));
    }

    @Test
    void listAllLeaveRequests_success() {
        Page<LeaveRequest> page = new PageImpl<>(List.of(leaveRequest));
        when(leaveRequestRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(anyList()))
                .thenReturn(List.of(mock(ListLeaveRequestResponse.class)));

        PaginatedResponse<ListLeaveRequestResponse> response = service.listAllLeaveRequests(0, 10, "createdAt", "desc", null);
        assertEquals(1, response.data().size());
    }

    @Test
    void getMyLeaveRequests_success() {
        Page<LeaveRequest> page = new PageImpl<>(List.of(leaveRequest));
        when(leaveRequestRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(page);
        when(leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(anyList()))
                .thenReturn(List.of(mock(ListLeaveRequestResponse.class)));

        PaginatedResponse<ListLeaveRequestResponse> response = service.getMyLeaveRequests(employee.getEmail(), 0, 10, "createdAt", "desc", null);
        assertEquals(1, response.data().size());
    }

    @Test
    void getMyLeaveBalance_success() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(leaveRequestMapper.toEmployeeLeaveBalanceResponse(employee)).thenReturn(mock(EmployeeLeaveBalanceResponse.class));

        EmployeeLeaveBalanceResponse response = service.getMyLeaveBalance(employee.getEmail());
        assertNotNull(response);
    }

    @Test
    void getEmployeeLeaveBalance_success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveRequestMapper.toEmployeeLeaveBalanceResponse(employee)).thenReturn(mock(EmployeeLeaveBalanceResponse.class));

        EmployeeLeaveBalanceResponse response = service.getEmployeeLeaveBalance(employee.getId());
        assertNotNull(response);
    }

    @Test
    void getEmployeeLeaveBalance_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> service.getEmployeeLeaveBalance(id));
    }
}
