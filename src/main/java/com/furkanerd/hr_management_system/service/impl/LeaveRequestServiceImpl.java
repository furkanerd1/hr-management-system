package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.LeaveRequestAlreadyProcessedException;
import com.furkanerd.hr_management_system.exception.LeaveRequestNotFoundException;
import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository, LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public List<ListLeaveRequestResponse> listAllLeaveRequests() {
        return leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(leaveRequestRepository.findAll());
    }

    @Override
    // TODO : get one more parameter "UserPrincipal currentUser"
    public LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest ) {
        // Temporary - Ali's ID
        UUID uuid = UUID.fromString("3a58a1ad-e146-4248-8c7c-6cd7e7364f57");
        Employee employee = employeeRepository.findById(uuid)
                .orElseThrow(()-> new EmployeeNotFoundException(uuid));

        LeaveRequest toCreate = LeaveRequest.builder()
                .employee(employee)
                .leaveType(createRequest.leaveType())
                .startDate(createRequest.startDate())
                .endDate(createRequest.endDate())
                .reason(createRequest.reason())
                .status(LeaveStatusEnum.PENDING)
                .build();

        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(toCreate));
    }

    @Override
    public LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId, LeaveRequestUpdateRequest updateRequest, Employee approver) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException(""));

        if(leaveRequest.getStatus() != LeaveStatusEnum.PENDING) {
            throw new LeaveRequestAlreadyProcessedException(leaveRequestId,leaveRequest.getStatus());
        }

        // set information
        leaveRequest.setStatus(updateRequest.status());
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }

}
