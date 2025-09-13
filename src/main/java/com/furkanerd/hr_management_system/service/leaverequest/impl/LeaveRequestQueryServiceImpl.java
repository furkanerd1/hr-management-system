package com.furkanerd.hr_management_system.service.leaverequest.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.custom.LeaveRequestNotFoundException;
import com.furkanerd.hr_management_system.mapper.LeaveRequestMapper;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.LeaveRequestRepository;
import com.furkanerd.hr_management_system.service.leaverequest.LeaveRequestQueryService;
import com.furkanerd.hr_management_system.specification.LeaveRequestSpecification;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class LeaveRequestQueryServiceImpl implements LeaveRequestQueryService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestQueryServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository, LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @Override
    public PaginatedResponse<ListLeaveRequestResponse> listAllLeaveRequests(int page, int size, String sortBy, String sortDirection, LeaveRequestFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.LEAVE_REQUEST_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<LeaveRequest> spec = LeaveRequestSpecification.withFilters(filterRequest);

        Page<LeaveRequest> leaveRequestPage = (spec != null)
                ? leaveRequestRepository.findAll(spec, pageable)
                : leaveRequestRepository.findAll(pageable);

        List<ListLeaveRequestResponse> responseList = leaveRequestMapper.leaveRequestsToListLeaveRequestResponse(leaveRequestPage.getContent());

        return PaginatedResponse.of(
                responseList,
                leaveRequestPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public PaginatedResponse<ListLeaveRequestResponse> getMyLeaveRequests(String email, int page, int size, String sortBy, String sortDirection, LeaveRequestFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.LEAVE_REQUEST_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<LeaveRequest> baseSpec = LeaveRequestSpecification.withFilters(filterRequest);

        Specification<LeaveRequest> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("email"), email))
                : (root, query, cb) -> cb.equal(root.get("employee").get("email"), email);


        Page<LeaveRequest> leaveRequestPage = leaveRequestRepository.findAll(specification, pageable);
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
                .orElseThrow(() -> new LeaveRequestNotFoundException(id)));
    }

    @Override
    public EmployeeLeaveBalanceResponse getMyLeaveBalance(String email) {
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException(email));
        return leaveRequestMapper.toEmployeeLeaveBalanceResponse(employee);
    }

    @Override
    public EmployeeLeaveBalanceResponse getEmployeeLeaveBalance(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        return leaveRequestMapper.toEmployeeLeaveBalanceResponse(employee);
    }
}
