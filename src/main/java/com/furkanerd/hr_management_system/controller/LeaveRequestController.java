package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
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

    @Operation(summary = "Get all leave requests", description = "Retrieves a list of all leave requests. Restricted to HR and Manager roles.")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListLeaveRequestResponse>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.listAllLeaveRequests());
    }

    @Operation(summary = "Get a single leave request by ID", description = "Retrieves a single leave request by its unique ID. Restricted to HR and Manager roles.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<LeaveRequestDetailResponse> getLeaveRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestById(id));
    }


    @Operation(summary = "Get authenticated user's leave requests", description = "Retrieves a list of all leave requests for the authenticated user. Accessible to all employees.")
    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ListLeaveRequestResponse>> getMyLeaveRequests(@AuthenticationPrincipal UserDetails currentUser) {
        return ResponseEntity.ok(leaveRequestService.getMyLeaveRequests(currentUser.getUsername()));
    }

    @Operation(summary = "Create a new leave request", description = "Creates a new leave request for the authenticated employee. Accessible to all employees.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<LeaveRequestDetailResponse> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ){
        String requester = currentUser.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.createLeaveRequest(createRequest,requester));
    }

    @Operation(summary = "Edit an existing leave request", description = "Updates an existing leave request. Only the requester can update an unapproved request.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<LeaveRequestDetailResponse> editLeaveRequest(
            @PathVariable UUID id,
            @Valid @RequestBody LeaveRequestEditRequest editRequest,
            @AuthenticationPrincipal UserDetails currentUser) {
        String requesterEmail = currentUser.getUsername();
        return ResponseEntity.ok(leaveRequestService.editLeaveRequest(id, editRequest, requesterEmail));
    }

    @Operation(summary = "Approve a leave request", description = "Approves a specific leave request. The approving user's ID is automatically captured. This action is restricted to HR and Manager roles.")
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

    @Operation(summary = "Cancel a leave request", description = "Cancels an existing leave request. Only the requester can cancel their unapproved request.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<Void> cancelLeaveRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails currentUser) {
        String requesterEmail = currentUser.getUsername();
        leaveRequestService.cancelLeaveRequest(id, requesterEmail);
        return ResponseEntity.noContent().build();
    }
}
