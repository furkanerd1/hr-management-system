package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

import static com.furkanerd.hr_management_system.constants.ApiPaths.DEPARTMENTS;

@RestController
@RequestMapping(DEPARTMENTS)
@Tag(name = "Department", description = "Department management API")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(
            summary = "Get all departments",
            description = "Retrieves a list of all departments. Accessible by employees and all higher-level roles."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ListDepartmentResponse>>> getAllDepartments(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            DepartmentFilterRequest filterRequest
    ) {
        PaginatedResponse<ListDepartmentResponse> departments = departmentService.listAllDepartments(page, size, sortBy, sortDirection, filterRequest);
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved successfully", departments));
    }

    @Operation(
            summary = "Get a department by ID",
            description = "Retrieves a specific department's details using its unique ID. Accessible by employees and all higher-level roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<DepartmentDetailResponse>> getDepartmentById(@PathVariable("id") UUID departmentId) {
        DepartmentDetailResponse department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department retrieved successfully", department));
    }

    @Operation(
            summary = "Create a new department",
            description = "Creates a new department in the system. This action is restricted to users with the HR role."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<DepartmentDetailResponse>> createDepartment(@Valid @RequestBody DepartmentCreateRequest departmentCreateRequest) {
        DepartmentDetailResponse created = departmentService.createDepartment(departmentCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", created));
    }

    @Operation(
            summary = "Update an existing department",
            description = "Updates the details of an existing department using its unique ID. This action is restricted to users with the HR role."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<DepartmentDetailResponse>> updateDepartment(
            @PathVariable("id") UUID departmentId,
            @Valid @RequestBody DepartmentUpdateRequest departmentUpdateRequest) {
        DepartmentDetailResponse updated = departmentService.updateDepartment(departmentId, departmentUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully", updated));
    }

    @Operation(
            summary = "Delete a department",
            description = "Deletes a department from the system using its unique ID. This action is irreversible and restricted to users with the HR role."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable("id") UUID departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
    }
}
