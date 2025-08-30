package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.EMPLOYEES;

@RestController
@RequestMapping(EMPLOYEES)
@Tag(name = "Employee", description = "Employee management API")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get current authenticated user's profile",
            description = "Retrieves the profile information for the currently authenticated employee. Accessible to all authenticated users (HR, Manager, and Employee)."
    )
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getMyProfile(@AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeDetailByEmail(email)));
    }


    @Operation(
            summary = "Get all employees",
            description = "Retrieves a list of all employee records. Accessible only to HR and Manager roles."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListEmployeeResponse>>>  getAllEmployees(
            @RequestParam(defaultValue = "0") int page ,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        PaginatedResponse<ListEmployeeResponse> result = employeeService.listAllEmployees(page,size,sortBy,sortDirection);

        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", result));
    }


    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves a specific employee record using their unique ID. Accessible only to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getEmployee(@PathVariable("id") UUID employeeId){
        return ResponseEntity.ok(ApiResponse.success( employeeService.getEmployeeById(employeeId)));
    }


    @Operation(
            summary = "Update an employee's profile",
            description = "Updates the profile of a specific employee. Employees can only update their own profile. HR and Managers can update any employee's profile."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE')")
    public  ResponseEntity<ApiResponse<EmployeeDetailResponse>> updateEmployee(
            @PathVariable("id") UUID employeeIdToUpdate,
            @Valid @RequestBody EmployeeUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetails currentUser
            ){
        String updaterEmail = currentUser.getUsername();
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", employeeService.updateEmployee(employeeIdToUpdate, updateRequest, updaterEmail)));
    }

    @Operation(
            summary = "Get employee salary history",
            description = "Retrieves the salary history for a specific employee by ID. Accessible only to HR and Manager roles."
    )
    @GetMapping("/{employeeId}/salaries")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListSalaryResponse>>> getEmployeeSalaryHistory(
            @PathVariable UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "effectiveDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection

    ){
        PaginatedResponse<ListSalaryResponse> responseList = employeeService
                .getEmployeeSalaryHistory(employeeId,page,size,sortBy,sortDirection);

        return ResponseEntity.ok(ApiResponse.success(responseList));
    }


    @Operation(
            summary = "Get performance history for a specific employee",
            description = "Retrieves a list of performance reviews for a specified employee by ID. This action is restricted to users with the HR or Manager role.")
    @GetMapping("/{id}/performance-history")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public  ResponseEntity<ApiResponse<PaginatedResponse<ListPerformanceReviewResponse>>> getEmployeePerformanceHistory(
            @PathVariable("id") UUID employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        PaginatedResponse<ListPerformanceReviewResponse> responseList =
                employeeService.getPerformanceReviewsByEmployeeId(employeeId, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(ApiResponse.success("Performance reviews retrieved successfully", responseList));
    }

    @Operation(
            summary = "Get employee attendance history",
            description = "Retrieves the attendance history for a specific employee by ID. Accessible only to HR and Manager roles."
    )
    @GetMapping("/{id}/attendance-history")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListAttendanceResponse>>> getEmployeeAttendanceHistory(
            @PathVariable("id") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        PaginatedResponse<ListAttendanceResponse> responseList = employeeService
                .getAllAttendanceByEmployeeId(id,page,size,sortBy,sortDirection);

        return  ResponseEntity.ok(ApiResponse.success(responseList));
    }

    @Operation(
            summary = "Get an employee's leave balance",
            description = "Retrieves the leave balance (vacation and maternity) for a specific employee by ID. Accessible to HR, Manager, and the employee themselves.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{id}/leave-balance")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeLeaveBalanceResponse>> getLeaveBalance(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getLeaveBalance(id)));
    }


    @Operation(
            summary = "Get authenticated user's leave balance",
            description = "Retrieves the leave balance for the authenticated user only. Accessible to all employees.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-leave-balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeLeaveBalanceResponse>> getMyLeaveBalance(@AuthenticationPrincipal UserDetails currentUser) {
        Employee employee = employeeService.getEmployeeEntityByEmail(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success(employeeService.getLeaveBalance(employee.getId())));
    }
}
