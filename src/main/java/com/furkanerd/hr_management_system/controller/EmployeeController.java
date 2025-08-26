package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<EmployeeDetailResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String email = currentUser.getUsername();
        return ResponseEntity.ok(employeeService.getEmployeeDetailByEmail(email));
    }


    @Operation(
            summary = "Get all employees",
            description = "Retrieves a list of all employee records. Accessible only to HR and Manager roles."
    )
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListEmployeeResponse>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.listAllEmployees());
    }


    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves a specific employee record using their unique ID. Accessible only to HR and Manager roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<EmployeeDetailResponse> getEmployee(@PathVariable("id") UUID employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }


    @Operation(
            summary = "Update an employee's profile",
            description = "Updates the profile of a specific employee. Employees can only update their own profile. HR and Managers can update any employee's profile."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<EmployeeDetailResponse> updateEmployee(
            @PathVariable("id") UUID employeeIdToUpdate,
            @Valid @RequestBody EmployeeUpdateRequest updateRequest,
            @AuthenticationPrincipal UserDetails currentUser
            ){
        String updaterEmail = currentUser.getUsername();
        return ResponseEntity.ok(employeeService.updateEmployee(employeeIdToUpdate, updateRequest, updaterEmail));
    }

    @GetMapping("/{employeeId}/salaries")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListSalaryResponse>> getEmployeeSalaryHistory(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(employeeService.getEmployeeSalaryHistory(employeeId));
    }


    @Operation(summary = "Get performance history for a specific employee",
            description = "Retrieves a list of performance reviews for a specified employee by ID. This action is restricted to users with the HR or Manager role.")
    @GetMapping("/{id}/performance-history")
    @PreAuthorize("hasAnyAuthority('ROLE_HR', 'ROLE_MANAGER')")
    public ResponseEntity<List<ListPerformanceReviewResponse>> getEmployeePerformanceHistory(@PathVariable("id") UUID employeeId) {
        return ResponseEntity.ok(employeeService.getPerformanceReviewsByEmployeeId(employeeId));
    }

}
