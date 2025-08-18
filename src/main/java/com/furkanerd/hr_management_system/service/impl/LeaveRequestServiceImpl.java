package com.furkanerd.hr_management_system.service.impl;

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
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.EmployeeService;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeService employeeService;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository,LeaveRequestMapper leaveRequestMapper, EmployeeService employeeService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeService = employeeService;
    }

    @Override
    public List<ListLeaveRequestResponse> listAllLeaveRequests() {
        return leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(leaveRequestRepository.findAll());
    }

    @Override
    @Transactional
    public LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest , String email) {

        Employee employee = employeeService.getEmployeeByEmail(email);

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
    @Transactional
    public LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId, LeaveRequestUpdateRequest updateRequest, String approverEmail) {


        Employee approver = employeeService.getEmployeeByEmail(approverEmail);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found: " + leaveRequestId));

        if(!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new LeaveRequestAlreadyProcessedException(leaveRequestId,leaveRequest.getStatus());
        }

        // set information
        leaveRequest.setStatus(updateRequest.status());
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }

}
