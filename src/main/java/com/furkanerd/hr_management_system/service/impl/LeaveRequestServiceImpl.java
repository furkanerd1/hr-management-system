package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.InsufficientLeaveBalanceException;
import com.furkanerd.hr_management_system.exception.LeaveRequestAlreadyProcessedException;
import com.furkanerd.hr_management_system.exception.LeaveRequestNotFoundException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.EmployeeService;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public PaginatedResponse<ListLeaveRequestResponse> listAllLeaveRequests(int page,int size,String sortBy,String sortDirection) {
        String validatedSortBy = SortFieldValidator.validate("leaveRequest",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Page<LeaveRequest> leaveRequestPage = leaveRequestRepository.findAll(pageable);
        List<ListLeaveRequestResponse> responseList = leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(leaveRequestPage.getContent());

        return PaginatedResponse.of(
                responseList,
                leaveRequestPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public LeaveRequestDetailResponse getLeaveRequestById(UUID id) {
        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveRequestNotFoundException("LeaveRequestNotFoundException")));
    }

    @Override
    public PaginatedResponse<ListLeaveRequestResponse> getMyLeaveRequests(String email,int page,int size, String sortBy,String sortDirection) {
        String validatedSortBy = SortFieldValidator.validate("leaveRequest",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Page<LeaveRequest> leaveRequestPage = leaveRequestRepository.findAllByEmployeeEmail(email,pageable);
        List<ListLeaveRequestResponse> responseList = leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(leaveRequestPage.getContent());

        return PaginatedResponse.of(
                responseList,
                leaveRequestPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    @Transactional
    public LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest , String email) {

        Employee requester = employeeService.getEmployeeEntityByEmail(email);

        int totalDays = (int) ChronoUnit.DAYS.between(createRequest.startDate(),createRequest.endDate()) + 1;

        switch (createRequest.leaveType()) {
            case VACATION -> {
                if (requester.getVacationBalance() < totalDays) {
                    throw new InsufficientLeaveBalanceException("Employee does not have enough vacation days.");
                }
            }
            case MATERNITY -> {
                if (requester.getMaternityBalance() < totalDays) {
                    throw new InsufficientLeaveBalanceException("Employee does not have enough maternity leave days.");
                }
            }
            case SICK, UNPAID -> {
            }
        }

        LeaveRequest toCreate = LeaveRequest.builder()
                .employee(requester)
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
    public LeaveRequestDetailResponse editLeaveRequest(UUID leaveRequestId, LeaveRequestEditRequest editRequest, String requesterEmail) {

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() -> new LeaveRequestNotFoundException("Leave request with id " + leaveRequestId + " not found"));

        if (!leaveRequest.getEmployee().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedActionException("You can only edit your own leave request");
        }

        if(!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)){
            throw new UnauthorizedActionException("It is not possible to edit leave request");
        }

        leaveRequest.setLeaveType(editRequest.leaveType());
        leaveRequest.setStartDate(editRequest.startDate());
        leaveRequest.setEndDate(editRequest.endDate());
        leaveRequest.setReason(editRequest.reason());

        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    @Transactional
    public LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId,String approverEmail) {

        Employee approver = employeeService.getEmployeeEntityByEmail(approverEmail);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found: " + leaveRequestId));

        if(!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new LeaveRequestAlreadyProcessedException(leaveRequestId, leaveRequest.getStatus());
        }

        Employee employee = leaveRequest.getEmployee();
        int totalDays = leaveRequest.getTotalDays();

        switch (leaveRequest.getLeaveType()) {
            case VACATION -> {
                if (employee.getVacationBalance() < totalDays) {
                    leaveRequest.setStatus(LeaveStatusEnum.REJECTED);
                } else {
                    leaveRequest.setStatus(LeaveStatusEnum.APPROVED);
                    employee.setVacationBalance(employee.getVacationBalance() - totalDays);
                }
            }
            case MATERNITY -> {
                if (employee.getMaternityBalance() < totalDays) {
                    leaveRequest.setStatus(LeaveStatusEnum.REJECTED);
                } else {
                    leaveRequest.setStatus(LeaveStatusEnum.APPROVED);
                    employee.setMaternityBalance(employee.getMaternityBalance() - totalDays);
                }
            }
            case SICK, UNPAID -> leaveRequest.setStatus(LeaveStatusEnum.APPROVED);
        }

        leaveRequest.setApprover(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        employeeService.saveEmployee(employee);
        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    @Transactional
    public LeaveRequestDetailResponse rejectLeaveRequest(UUID leaveRequestId, String approverEmail) {
        Employee approver = employeeService.getEmployeeEntityByEmail(approverEmail);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found: " + leaveRequestId));

        if (!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new LeaveRequestAlreadyProcessedException(leaveRequestId, leaveRequest.getStatus());
        }

        leaveRequest.setStatus(LeaveStatusEnum.REJECTED);
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }

    @Override
    @Transactional
    public void cancelLeaveRequest(UUID leaveRequestId, String requesterEmail) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request not found"));

        if (!leaveRequest.getEmployee().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedActionException("You can only cancel your own leave request");
        }

        if (!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new UnauthorizedActionException("Only pending leave requests can be cancelled");
        }

        leaveRequestRepository.delete(leaveRequest);
    }
}
