package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import static com.furkanerd.hr_management_system.config.ApiPaths.ATTENDANCE;

@RestController
@RequestMapping(ATTENDANCE)
@Tag(name = "Attendance Management", description = "Operations related to employee attendance records")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Operation(
            summary = "List all attendances",
            description = "Returns a list of all attendance records."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    // TODO: Convert to PaginatedResponse when pagination is implemented
    public ResponseEntity<ApiResponse<List<ListAttendanceResponse>>> getAttendance(){
        List<ListAttendanceResponse> attendances = attendanceService.listAllAttendance();
        return ResponseEntity.ok(ApiResponse.success("Attendance records retrieved successfully", attendances));
    }

    @Operation(
            summary = "Get a single attendance record by ID",
            description = "Retrieves a specific attendance record using its unique ID. Restricted to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> getAttendanceById(@PathVariable UUID id) {
        AttendanceDetailResponse attendance = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record retrieved successfully", attendance));
    }

    @Operation(
            summary = "Create manual attendance record",
            description = "Creates an attendance record manually for a given employee. Role restricted to HR/Manager."
    )
    @PostMapping("/manual")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> createAttendance(@Valid @RequestBody AttendanceCreateRequest attendanceCreateRequest) {
        AttendanceDetailResponse created = attendanceService.createAttendance(attendanceCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance record created successfully", created));
    }

    @Operation(
            summary = "Check-in for today",
            description = "Automatically registers check-in for the employee. Restricted to the employee themselves."
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> checkIn(@AuthenticationPrincipal UserDetails currentUser) {
        String employeeEmail = currentUser.getUsername();
        AttendanceDetailResponse checkedIn = attendanceService.autoCheckIn(employeeEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Check-in successful", checkedIn));
    }

    @Operation(
            summary = "Check-out for today",
            description = "Automatically registers check-out for the employee. Restricted to the employee themselves."
    )
    @PostMapping("/check-out")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>>  checkOut(@AuthenticationPrincipal UserDetails currentUser) {
        String employeeEmail = currentUser.getUsername();
        AttendanceDetailResponse checkedOut = attendanceService.autoCheckOut(employeeEmail);
        return ResponseEntity.ok(ApiResponse.success("Check-out successful", checkedOut));
    }


    @Operation(
            summary = "Update an attendance record by ID",
            description = "Updates an existing attendance record for an employee. Restricted to HR and Manager roles."
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<AttendanceDetailResponse>> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceUpdateRequest updateRequest
    ){
        AttendanceDetailResponse updated = attendanceService.updateAttendance(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Attendance record updated successfully", updated));
    }


    @Operation(
            summary = "Get my attendance records",
            description = "Returns attendance records for the authenticated employee."
    )
    @GetMapping("/my-attendance")
    @PreAuthorize("isAuthenticated()")
    // TODO: Convert to PaginatedResponse when pagination is implemented
    public ResponseEntity<ApiResponse<List<ListAttendanceResponse>>> getMyAttendances(@AuthenticationPrincipal UserDetails currentUser){
        String employeeEmail = currentUser.getUsername();
        List<ListAttendanceResponse> myAttendances = attendanceService.getAttendanceByEmployee(employeeEmail);
        return ResponseEntity.ok(ApiResponse.success("My attendance records retrieved successfully", myAttendances));
    }

    @Operation(
            summary = "Delete an attendance record by ID",
            description = "Deletes a specific attendance record using its unique ID. Restricted to HR and Manager roles."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(@PathVariable UUID id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok(ApiResponse.success("Attendance record deleted successfully"));
    }
}
