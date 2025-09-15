package com.furkanerd.hr_management_system.service.leaverequest.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.model.enums.LeaveStatusEnum;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.leaverequest.LeaveRequestManagementService;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
class LeaveRequestManagementServiceImpl implements LeaveRequestManagementService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    LeaveRequestManagementServiceImpl(LeaveRequestRepository leaveRequestRepository, LeaveRequestMapper leaveRequestMapper, EmployeeRepository employeeRepository, NotificationService notificationService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
    }

    @Override
    public LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest, String requesterEmail) {
        Employee requester = getEmployeeByEmail(requesterEmail);
        validateLeaveBalance(createRequest, requester);

        LeaveRequest leaveRequest = buildLeaveRequest(createRequest, requester);
        LeaveRequestDetailResponse response = saveAndMap(leaveRequest);

        notificationService.notify(requester,
                "Leave Request Submitted",
                "Your leave request from " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() + " has been submitted.",
                NotificationTypeEnum.LEAVE);
        return response;
    }

    @Override
    public LeaveRequestDetailResponse editLeaveRequest(UUID leaveRequestId, LeaveRequestEditRequest editRequest, String requesterEmail) {
        LeaveRequest leaveRequest = getPendingLeaveRequestForEmployee(leaveRequestId, requesterEmail);

        leaveRequest.setLeaveType(editRequest.leaveType());
        leaveRequest.setStartDate(editRequest.startDate());
        leaveRequest.setEndDate(editRequest.endDate());
        leaveRequest.setReason(editRequest.reason());

        return saveAndMap(leaveRequest);
    }

    @Override
    public LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId, String approverEmail) {
        LeaveRequest leaveRequest = getPendingLeaveRequest(leaveRequestId);
        Employee approver = getEmployeeByEmail(approverEmail);

        processApproval(leaveRequest, approver);
        LeaveRequestDetailResponse response = saveAndMap(leaveRequest);

        notificationService.notify(leaveRequest.getEmployee(),
                "Leave Request Approved",
                "Your leave request from " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() + " has been approved by " + approver.getFirstName() + " " + approver.getLastName() + ".",
                NotificationTypeEnum.LEAVE);

        return response;
    }

    @Override
    public LeaveRequestDetailResponse rejectLeaveRequest(UUID leaveRequestId, String approverEmail) {
        LeaveRequest leaveRequest = getPendingLeaveRequest(leaveRequestId);
        Employee approver = getEmployeeByEmail(approverEmail);

        leaveRequest.setStatus(LeaveStatusEnum.REJECTED);
        leaveRequest.setApprover(approver);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        LeaveRequestDetailResponse response = saveAndMap(leaveRequest);

        notificationService.notify(leaveRequest.getEmployee(),
                "Leave Request Rejected",
                "Your leave request from " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() + " has been rejected by " + approver.getFirstName() + " " + approver.getLastName() + ".",
                NotificationTypeEnum.LEAVE);

        return response;
    }

    @Override
    public void cancelLeaveRequest(UUID leaveRequestId, String requesterEmail) {
        LeaveRequest leaveRequest = getLeaveRequestEntity(leaveRequestId);

        if (!leaveRequest.getEmployee().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedActionException("You can only cancel your own leave request");
        }

        if (!leaveRequest.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new UnauthorizedActionException("Only pending leave requests can be cancelled");
        }

        leaveRequestRepository.delete(leaveRequest);

        notificationService.notify(leaveRequest.getEmployee(),
                "Leave Request Cancelled",
                "Your leave request from " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() + " has been cancelled.",
                NotificationTypeEnum.LEAVE);
    }


    private Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    private LeaveRequest getLeaveRequestEntity(UUID leaveRequestId) {
        return leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new LeaveRequestNotFoundException(leaveRequestId));
    }

    private LeaveRequest getPendingLeaveRequest(UUID leaveRequestId) {
        LeaveRequest lr = getLeaveRequestEntity(leaveRequestId);
        if (!lr.getStatus().equals(LeaveStatusEnum.PENDING)) {
            throw new LeaveRequestAlreadyProcessedException(leaveRequestId, lr.getStatus());
        }
        return lr;
    }

    private LeaveRequest getPendingLeaveRequestForEmployee(UUID leaveRequestId, String email) {
        LeaveRequest lr = getPendingLeaveRequest(leaveRequestId);
        if (!lr.getEmployee().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You can only edit your own leave request");
        }
        return lr;
    }

    private void validateLeaveBalance(LeaveRequestCreateRequest request, Employee employee) {
        int totalDays = (int) ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;

        switch (request.leaveType()) {
            case VACATION -> {
                if (employee.getVacationBalance() < totalDays) {
                    throw new InsufficientLeaveBalanceException(request.leaveType().toString(), employee.getVacationBalance(), totalDays);
                }
            }
            case MATERNITY -> {
                if (employee.getMaternityBalance() < totalDays) {
                    throw new InsufficientLeaveBalanceException(request.leaveType().toString(), employee.getVacationBalance(), totalDays);
                }
            }
        }
    }

    private LeaveRequest buildLeaveRequest(LeaveRequestCreateRequest request, Employee employee) {
        return LeaveRequest.builder()
                .employee(employee)
                .leaveType(request.leaveType())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .reason(request.reason())
                .status(LeaveStatusEnum.PENDING)
                .build();
    }

    private void processApproval(LeaveRequest leaveRequest, Employee approver) {
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
        employeeRepository.save(employee);
    }

    private LeaveRequestDetailResponse saveAndMap(LeaveRequest leaveRequest) {
        return leaveRequestMapper.leaveRequestToLeaveRequestDetailResponse(leaveRequestRepository.save(leaveRequest));
    }
}
