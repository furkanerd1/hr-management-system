package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.furkanerd.hr_management_system.config.ApiPaths.SALARIES;

@RestController
@RequestMapping(SALARIES)
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    // TODO: ROLE BASED(HR)
    public ResponseEntity<List<ListSalaryResponse>> getSalaries(){
        return ResponseEntity.ok(salaryService.listAllSalaries());
    }

    @PostMapping
    // TODO: ROLE BASED(HR)
    public ResponseEntity<SalaryDetailResponse> createSalary(
            @Valid @RequestBody SalaryCreateRequest salaryCreateRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(salaryService.createSalary(salaryCreateRequest));

    }
}
