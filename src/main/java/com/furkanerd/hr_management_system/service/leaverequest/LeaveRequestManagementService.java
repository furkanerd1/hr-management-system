package com.furkanerd.hr_management_system.service.leaverequest;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;

import java.util.UUID;

public interface LeaveRequestManagementService {
    LeaveRequestDetailResponse createLeaveRequest(LeaveRequestCreateRequest createRequest, String requesterEmail);

    LeaveRequestDetailResponse editLeaveRequest(UUID leaveRequestId, LeaveRequestEditRequest editRequest, String requesterEmail);

    LeaveRequestDetailResponse approveLeaveRequest(UUID leaveRequestId, String approverEmail);

    LeaveRequestDetailResponse rejectLeaveRequest(UUID leaveRequestId, String approverEmail);

    void cancelLeaveRequest(UUID leaveRequestId, String requesterEmail);
}
