package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface LeaveRequestService {

    List<ListLeaveRequestResponse> listAllLeaveRequests();

    LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest);

    LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId , LeaveRequestUpdateRequest updateRequest , Employee approver);
}
