package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestEditRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.furkanerd.hr_management_system.constants.ApiPaths.*;


@RestController
@RequestMapping(LEAVES)
@Tag(name = "Leave Request", description = "Employee leave request management API")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @Operation(
            summary = "Get all leave requests",
            description = "Retrieves a list of all leave requests. Restricted to HR and Manager roles."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListLeaveRequestResponse>>> getAllLeaveRequests(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            LeaveRequestFilterRequest filterRequest
    ) {
        PaginatedResponse<ListLeaveRequestResponse> responseList = leaveRequestService.listAllLeaveRequests(page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("Leave requests retrieved successfully", responseList));
    }

    @Operation(
            summary = "Get a single leave request by ID",
            description = "Retrieves a single leave request by its unique ID. Restricted to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<LeaveRequestDetailResponse>> getLeaveRequestById(@PathVariable UUID id) {
        LeaveRequestDetailResponse leave = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Leave request retrieved successfully", leave));
    }


    @Operation(
            summary = "Get authenticated user's leave requests",
            description = "Retrieves a list of all leave requests for the authenticated user. Accessible to all employees."
    )
    @GetMapping("/my-requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListLeaveRequestResponse>>> getMyLeaveRequests(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            LeaveRequestFilterRequest filterRequest

    ) {
        PaginatedResponse<ListLeaveRequestResponse> responseList = leaveRequestService
                .getMyLeaveRequests(currentUser.getUsername(), page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("My leave requests retrieved successfully", responseList));
    }

    @Operation(
            summary = "Get authenticated user's leave balance",
            description = "Retrieves the leave balance for the authenticated user."
    )
    @GetMapping("/my-balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeLeaveBalanceResponse>> getMyLeaveBalance(@AuthenticationPrincipal UserDetails currentUser) {
        EmployeeLeaveBalanceResponse balance = leaveRequestService.getMyLeaveBalance(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Leave balance retrieved successfully", balance));
    }


    @Operation(
            summary = "Get employee's leave balance by ID",
            description = "Retrieves the leave balance for a specific employee. Restricted to HR and Manager roles."
    )
    @GetMapping("/{employeeId}/balance")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeLeaveBalanceResponse>> getEmployeeLeaveBalance(@PathVariable UUID employeeId) {
        EmployeeLeaveBalanceResponse balance = leaveRequestService.getEmployeeLeaveBalance(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Leave balance retrieved successfully", balance));
    }


    @Operation(
            summary = "Create a new leave request",
            description = "Creates a new leave request for the authenticated employee. Accessible to all employees."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveRequestDetailResponse>> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String requester = currentUser.getUsername();
        LeaveRequestDetailResponse created = leaveRequestService.createLeaveRequest(createRequest, requester);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave request created successfully", created));
    }

    @Operation(
            summary = "Edit an existing leave request",
            description = "Updates an existing leave request. Only the requester can update an unapproved request."
    )
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LeaveRequestDetailResponse>> editLeaveRequest(
            @PathVariable UUID id,
            @Valid @RequestBody LeaveRequestEditRequest editRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String requesterEmail = currentUser.getUsername();
        LeaveRequestDetailResponse updated = leaveRequestService.editLeaveRequest(id, editRequest, requesterEmail);
        return ResponseEntity.ok(ApiResponse.success("Leave request updated successfully", updated));
    }

    @Operation(
            summary = "Approve a leave request",
            description = "Approves a specific leave request. The approving user's ID is automatically captured. This action is restricted to HR and Manager roles."
    )
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<LeaveRequestDetailResponse>> approveLeaveRequest(
            @PathVariable("id") UUID leaveRequestId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String approverEmail = currentUser.getUsername();
        LeaveRequestDetailResponse approved = leaveRequestService.approveLeaveRequest(leaveRequestId, approverEmail);
        return ResponseEntity.ok(ApiResponse.success("Leave request approved successfully", approved));
    }

    @Operation(
            summary = "Reject a leave request",
            description = "Rejects a specific leave request. The approving user's ID is automatically captured. This action is restricted to HR and Manager roles."
    )
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<LeaveRequestDetailResponse>> rejectLeaveRequest(
            @PathVariable("id") UUID leaveRequestId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String rejecterEmail = currentUser.getUsername();
        LeaveRequestDetailResponse rejected = leaveRequestService.rejectLeaveRequest(leaveRequestId, rejecterEmail);
        return ResponseEntity.ok(ApiResponse.success("Leave request rejected successfully", rejected));
    }

    @Operation(
            summary = "Cancel a leave request",
            description = "Cancels an existing leave request. Only the requester can cancel their unapproved request."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> cancelLeaveRequest(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser) {

        String requesterEmail = currentUser.getUsername();
        leaveRequestService.cancelLeaveRequest(id, requesterEmail);
        return ResponseEntity.ok(ApiResponse.success("Leave request cancelled successfully"));
    }
}
