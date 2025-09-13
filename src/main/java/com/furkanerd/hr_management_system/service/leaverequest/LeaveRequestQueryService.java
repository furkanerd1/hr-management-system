package com.furkanerd.hr_management_system.service.leaverequest;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;

import java.util.UUID;

public interface LeaveRequestQueryService {

    PaginatedResponse<ListLeaveRequestResponse> listAllLeaveRequests(int page, int size, String sortBy, String sortDirection, LeaveRequestFilterRequest filterRequest);

    PaginatedResponse<ListLeaveRequestResponse> getMyLeaveRequests(String email, int page, int size, String sortBy, String sortDirection, LeaveRequestFilterRequest filterRequest);

    LeaveRequestDetailResponse getLeaveRequestById(UUID id);

    EmployeeLeaveBalanceResponse getMyLeaveBalance(String email);

    EmployeeLeaveBalanceResponse getEmployeeLeaveBalance(UUID employeeId);
}
