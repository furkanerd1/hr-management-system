package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.furkanerd.hr_management_system.config.ApiPaths.ATTENDANCE;

@RestController
@RequestMapping(ATTENDANCE)
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Operation(
            summary = "List all attendances",
            description = "Returns a list of all attendance records.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved attendances",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ListAttendanceResponse.class))))
            }
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListAttendanceResponse>> getAttendance(){
        return ResponseEntity.ok(attendanceService.listAllAttendance());
    }

    @Operation(
            summary = "Create manual attendance record",
            description = "Creates an attendance record manually for a given employee. Role restricted to HR/Manager.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Attendance created successfully",
                            content = @Content(schema = @Schema(implementation = AttendanceDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Employee not found"),
                    @ApiResponse(responseCode = "409", description = "Attendance already exists for this date"),
                    @ApiResponse(responseCode = "400", description = "Invalid check-in/check-out time")
            }
    )
    @PostMapping("/manual")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<AttendanceDetailResponse> createAttendance(
            @Valid @RequestBody AttendanceCreateRequest attendanceCreateRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(attendanceCreateRequest));
    }

    @Operation(
            summary = "Check-in for today",
            description = "Automatically registers check-in for the employee. Restricted to the employee themselves.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Check-in successful",
                            content = @Content(schema = @Schema(implementation = AttendanceDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Employee not found"),
                    @ApiResponse(responseCode = "409", description = "Attendance already exists for today")
            }
    )
    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<AttendanceDetailResponse> checkIn(@AuthenticationPrincipal UserDetails currentUser) {
        String employeeEmail =  currentUser.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceService.autoCheckIn(employeeEmail));
    }

    @Operation(
            summary = "Check-out for today",
            description = "Automatically registers check-out for the employee. Restricted to the employee themselves.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Check-out successful",
                            content = @Content(schema = @Schema(implementation = AttendanceDetailResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Attendance not found for today"),
                    @ApiResponse(responseCode = "400", description = "Already checked out or invalid check-out time")
            }
    )
    @PostMapping("/check-out")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<AttendanceDetailResponse> checkOut(@AuthenticationPrincipal UserDetails currentUser) {
        String employeeEmail =  currentUser.getUsername();
        return ResponseEntity.ok(attendanceService.autoCheckOut(employeeEmail));
    }


    @Operation(
            summary = "Get my attendance records",
            description = "Returns attendance records for the authenticated employee.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved attendances",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ListAttendanceResponse.class))))
            }
    )
    @GetMapping("/my-attendance")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<List<ListAttendanceResponse>> getMyAttendance(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String employeeEmail = currentUser.getUsername();
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployee(employeeEmail));
    }


}
