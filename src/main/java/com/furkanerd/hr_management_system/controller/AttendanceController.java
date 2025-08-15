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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    // TODO: ROLE BASED(HR,MANAGER)
    public ResponseEntity<AttendanceDetailResponse> createAttendance(
            @Valid @RequestBody AttendanceCreateRequest attendanceCreateRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.createAttendance(attendanceCreateRequest));
    }

    @PostMapping("/check-in")
    // TODO: securıty context ıd
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
    // TODO: ROLE BASED(SELF)
    // TODO: securıty context ıd
    public ResponseEntity<AttendanceDetailResponse> checkIn(@RequestParam UUID employeeId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceService.autoCheckIn(employeeId));
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
    // TODO: ROLE BASED(SELF)
    // TODO: securıty context ıd
    public ResponseEntity<AttendanceDetailResponse> checkOut(@RequestParam UUID employeeId) {
        return ResponseEntity.ok(attendanceService.autoCheckOut(employeeId));
    }

}
