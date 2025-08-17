package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.service.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.furkanerd.hr_management_system.config.ApiPaths.SALARIES;

@RestController
@RequestMapping(SALARIES)
@Tag(name = "Salary", description = "Salary management API")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @Operation(summary = "Get all salaries",
            description = "Retrieves a list of all employee salaries. This is a highly sensitive operation and is restricted to users with the HR role.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<List<ListSalaryResponse>> getSalaries(){
        return ResponseEntity.ok(salaryService.listAllSalaries());
    }

    @Operation(summary = "Create a new salary",
              description = "Creates a new salary record for an employee. This action is restricted to users with the HR role.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<SalaryDetailResponse> createSalary(
            @Valid @RequestBody SalaryCreateRequest salaryCreateRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createSalary(salaryCreateRequest));

    }
}
