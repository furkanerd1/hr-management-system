package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.DEPARTMENTS;
import static com.furkanerd.hr_management_system.config.ApiPaths.DEPARTMENTS_BY_ID;

@RestController
@RequestMapping(DEPARTMENTS)
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<ListDepartmentResponse>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.listAllDepartments());
    }

    @GetMapping(DEPARTMENTS_BY_ID)
    public ResponseEntity<DepartmentDetailResponse> getDepartmentById(@PathVariable("id") UUID departmentId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(departmentId));
    }

    @PostMapping
    //TODO : Role based ( HR)
    public ResponseEntity<DepartmentDetailResponse> createDepartment(
            @Valid  @RequestBody DepartmentCreateRequest departmentCreateRequest){
        return  ResponseEntity.ok(departmentService.createDepartment(departmentCreateRequest));
    }

    @PutMapping(DEPARTMENTS_BY_ID)
    //TODO : Role based ( HR)
    public ResponseEntity<DepartmentDetailResponse> updateDepartment(
            @PathVariable("id") UUID departmentId ,
            @Valid @RequestBody DepartmentUpdateRequest departmentUpdateRequest){
        return ResponseEntity.ok(departmentService.updateDepartment(departmentId, departmentUpdateRequest));
    }

    @DeleteMapping(DEPARTMENTS_BY_ID)
    //TODO : Role based ( HR)
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable("id") UUID departmentId){
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.ok().build();
    }
}
