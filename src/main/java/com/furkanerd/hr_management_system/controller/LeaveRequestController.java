package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.*;


@RestController
@RequestMapping(LEAVES)
@Tag(name = "Leave Request", description = "Employee leave request management API")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @Operation(summary = "Get all leave requests",
            description = "Retrieves a list of all leave requests. Restricted to HR and Manager roles.")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListLeaveRequestResponse>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.listAllLeaveRequests());
    }

    @Operation(summary = "Create a new leave request",
            description = "Creates a new leave request for the authenticated employee. Accessible to all employees.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<LeaveRequestDetailResponse> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ){
        String requester = currentUser.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.createLeaveRequest(createRequest,requester));
    }

    @Operation(summary = "Approve a leave request",
            description = "Approves a specific leave request. The approving user's ID is automatically captured. This action is restricted to HR and Manager roles.")
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<LeaveRequestDetailResponse> approveLeaveRequest(
            @PathVariable("id")UUID  leaveRequestId,
            @Valid @RequestBody LeaveRequestUpdateRequest leaveRequestUpdateRequest,
            @AuthenticationPrincipal UserDetails currentUser
            ){

        String approverEmail = currentUser.getUsername();

        LeaveRequestDetailResponse response = leaveRequestService.approveLeaveRequest(leaveRequestId,leaveRequestUpdateRequest,approverEmail);

        return ResponseEntity.ok(response);
    }
}
