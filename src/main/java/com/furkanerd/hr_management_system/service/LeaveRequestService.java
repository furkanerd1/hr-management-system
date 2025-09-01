package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;

import java.util.List;
import java.util.UUID;

public interface LeaveRequestService {

    PaginatedResponse<ListLeaveRequestResponse> listAllLeaveRequests(int page,int size,String sortBy,String sortDirection);

    LeaveRequestDetailResponse getLeaveRequestById(UUID id);

    PaginatedResponse<ListLeaveRequestResponse> getMyLeaveRequests(String email,int page,int size,String sortBy,String sortDirection);

    LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest, String email);

    LeaveRequestDetailResponse editLeaveRequest(UUID leaveRequestId,LeaveRequestEditRequest editRequest, String requesterEmail);

    LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId , String approverEmail);

    LeaveRequestDetailResponse rejectLeaveRequest(UUID leaveRequestId, String approverEmail);

    void cancelLeaveRequest(UUID leaveRequestId, String requesterEmail);
}
