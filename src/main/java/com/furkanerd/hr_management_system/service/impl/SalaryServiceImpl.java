package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.SalaryMapper;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.SalaryRepository;
import com.furkanerd.hr_management_system.service.SalaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryMapper salaryMapper;

    public SalaryServiceImpl(SalaryRepository salaryRepository, EmployeeRepository employeeRepository, SalaryMapper salaryMapper) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
        this.salaryMapper = salaryMapper;
    }

    @Override
    public List<ListSalaryResponse> listAllSalaries() {
        return salaryMapper.salariesToListSalaryResponses( salaryRepository.findAll() );
    }

    @Override
    public SalaryDetailResponse createSalary(SalaryCreateRequest createRequest) {
        UUID employeeId = createRequest.employeeId();
        Employee employee = employeeRepository.findById(createRequest.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        Salary toCreate = Salary.builder()
                .employee(employee)
                .salary(createRequest.salary())
                .bonus(createRequest.bonus())
                .effectiveDate(createRequest.effectiveDate())
                .build();

        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.save(toCreate));
    }
}
