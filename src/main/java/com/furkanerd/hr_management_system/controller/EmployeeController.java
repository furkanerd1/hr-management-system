package com.furkanerd.hr_management_system.controller;


import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.service.employee.EmployeeManagementService;
import com.furkanerd.hr_management_system.service.employee.EmployeeService;
import com.furkanerd.hr_management_system.service.employee.EmployeeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.furkanerd.hr_management_system.constants.ApiPaths.EMPLOYEES;

@RestController
@RequestMapping(EMPLOYEES)
@Tag(name = "Employee", description = "Employee management API")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeQueryService employeeQueryService;
    private final EmployeeManagementService employeeManagementService;

    public EmployeeController(EmployeeService employeeService, EmployeeQueryService employeeQueryService, EmployeeManagementService employeeManagementService) {
        this.employeeService = employeeService;
        this.employeeQueryService = employeeQueryService;
        this.employeeManagementService = employeeManagementService;
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
            summary = "Get all employees with pagination and filtering",
            description = "Retrieves a paginated list of employees with optional filtering"
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListEmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            EmployeeFilterRequest filterRequest
    ) {
        PaginatedResponse<ListEmployeeResponse> result = employeeQueryService.listAllEmployees(
                page, size, sortBy, sortDirection, filterRequest);

        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", result));
    }


    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves a specific employee record using their unique ID. Accessible only to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getEmployee(@PathVariable("id") UUID employeeId) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(employeeId)));
    }


    @Operation(
            summary = "Update an employee's profile",
            description = "Updates the profile of a specific employee. Employees can only update their own profile. HR and Managers can update any employee's profile."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> updateEmployee(
            @PathVariable("id") UUID employeeIdToUpdate,
            @Valid @RequestBody EmployeeUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String updaterEmail = currentUser.getUsername();
        return ResponseEntity.ok(ApiResponse.success("Updated successfully", employeeManagementService.updateEmployee(employeeIdToUpdate, updateRequest, updaterEmail)));
    }


    @Operation(
            summary = "Get all employees for a department",
            description = "Retrieves a list of all employees working in a specific department. Accessible to HR and Manager roles."
    )
    @GetMapping("/department/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListEmployeeResponse>>> getEmployeesByDepartment(
            @PathVariable("id") UUID departmentId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            EmployeeFilterRequest filterRequest
    ) {
        PaginatedResponse<ListEmployeeResponse> responseList = employeeQueryService.getEmployeesByDepartment(departmentId, page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved for department successfully", responseList));
    }
}
