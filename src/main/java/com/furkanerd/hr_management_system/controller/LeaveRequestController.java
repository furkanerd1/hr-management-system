package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.leaverequest.LeaveRequestUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.*;


@RestController
@RequestMapping(LEAVES)
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    // TODO: ROLE BASED(HR,MANAGER)
    public ResponseEntity<List<ListLeaveRequestResponse>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.listAllLeaveRequests());
    }

    @PostMapping
    // TODO : ROLE BASED(EMPLOYEE(SELF))
    public ResponseEntity<LeaveRequestDetailResponse> createLeaveRequest(
            @Valid @RequestBody LeaveRequestCreateRequest createRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.createLeaveRequest(createRequest));
    }

    @PatchMapping(LEAVES_APPROVE)
    // TODO : ROLE BASED(HR,MANAGER)
    // TODO : +param = @AuthenticationPrincipal UserPrincipal currentUser
    public ResponseEntity<LeaveRequestDetailResponse> approveLeaveRequest(
            @PathVariable("id")UUID  leaveRequestId,
            @Valid @RequestBody LeaveRequestUpdateRequest leaveRequestUpdateRequest
            ){
        return ResponseEntity.notFound().build();
    }
}
