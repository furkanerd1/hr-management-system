package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.service.SalaryService;
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

import static com.furkanerd.hr_management_system.config.ApiPaths.SALARIES;

@RestController
@RequestMapping(SALARIES)
@Tag(name = "Salary", description = "Salary management API")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @Operation(
            summary = "Get all salaries",
            description = "Retrieves a list of all employee salaries. This is a highly sensitive operation and is restricted to users with the HR role."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    // TODO: Replace with PaginatedResponse when pagination is added
    public ResponseEntity<ApiResponse<List<ListSalaryResponse>>> getSalaries(){
        return ResponseEntity.ok(ApiResponse.success(salaryService.listAllSalaries()));
    }


    @Operation(
            summary = "Get a single salary record by ID",
            description = "Retrieves a single salary record by its unique ID. This action is restricted to users with the HR role."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<SalaryDetailResponse>> getSalary(@PathVariable("id") UUID id){
        return ResponseEntity.ok(ApiResponse.success(salaryService.getSalaryById(id)));
    }

    @Operation(
            summary = "Create a new salary",
            description = "Creates a new salary record for an employee. This action is restricted to users with the HR role."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<SalaryDetailResponse>> createSalary(@Valid @RequestBody SalaryCreateRequest salaryCreateRequest) {
        SalaryDetailResponse createdSalary = salaryService.createSalary(salaryCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary created successfully", createdSalary));

    }

    @Operation(
            summary = "Get current user's salary history",
            description = "Retrieves the salary history for the authenticated user only. This endpoint is secured and requires a valid JWT token.")
    @GetMapping("/my-history")
    @PreAuthorize("isAuthenticated()")
    // TODO: Replace with PaginatedResponse when pagination is added
    public ResponseEntity<ApiResponse<List<ListSalaryResponse>>> getMySalaryHistory(@AuthenticationPrincipal UserDetails currentUser){
        String email = currentUser.getUsername();
        return ResponseEntity.ok(ApiResponse.success(salaryService.showEmployeeSalaryHistory(email)));
    }

    @Operation(
            summary = "Delete a salary record",
            description = "Deletes an existing salary record. This action is restricted to users with the HR role."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<Void>> deleteSalary(@PathVariable("id") UUID id){
        salaryService.deleteSalary(id);
        return ResponseEntity.ok(ApiResponse.success("Salary deleted successfully"));
    }
}
