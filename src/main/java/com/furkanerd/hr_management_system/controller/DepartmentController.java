package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.DEPARTMENTS;

@RestController
@RequestMapping(DEPARTMENTS)
@Tag(name = "Department", description = "Department management API")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "Get all departments",
            description = "Retrieves a list of all departments. Accessible by employees and all higher-level roles.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<List<ListDepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.listAllDepartments());
    }

    @Operation(summary = "Get a department by ID",
            description = "Retrieves a specific department's details using its unique ID. Accessible by employees and all higher-level roles.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<DepartmentDetailResponse> getDepartmentById(@PathVariable("id") UUID departmentId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(departmentId));
    }

    @Operation(summary = "Create a new department",
            description = "Creates a new department in the system. This action is restricted to users with the HR role.")

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<DepartmentDetailResponse> createDepartment(
            @Valid  @RequestBody DepartmentCreateRequest departmentCreateRequest){
        return  ResponseEntity.ok(departmentService.createDepartment(departmentCreateRequest));
    }

    @Operation(summary = "Update an existing department",
            description = "Updates the details of an existing department using its unique ID. This action is restricted to users with the HR role.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<DepartmentDetailResponse> updateDepartment(
            @PathVariable("id") UUID departmentId ,
            @Valid @RequestBody DepartmentUpdateRequest departmentUpdateRequest){
        return ResponseEntity.ok(departmentService.updateDepartment(departmentId, departmentUpdateRequest));
    }

    @Operation(summary = "Delete a department",
            description = "Deletes a department from the system using its unique ID. This action is irreversible and restricted to users with the HR role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable("id") UUID departmentId){
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok().build();
    }
}
