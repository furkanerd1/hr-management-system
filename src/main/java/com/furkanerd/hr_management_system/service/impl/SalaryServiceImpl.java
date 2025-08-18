package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.mapper.SalaryMapper;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.repository.SalaryRepository;
import com.furkanerd.hr_management_system.service.EmployeeService;
import com.furkanerd.hr_management_system.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final SalaryMapper salaryMapper;
    private final EmployeeService employeeService;

    public SalaryServiceImpl(SalaryRepository salaryRepository, SalaryMapper salaryMapper, EmployeeService employeeService) {
        this.salaryRepository = salaryRepository;
        this.salaryMapper = salaryMapper;
        this.employeeService = employeeService;
    }

    @Override
    public List<ListSalaryResponse> listAllSalaries() {
        return salaryMapper.salariesToListSalaryResponses( salaryRepository.findAll() );
    }

    @Override
    @Transactional
    public SalaryDetailResponse createSalary(SalaryCreateRequest createRequest) {
        Employee employee =employeeService.getEmployeeEntityById(createRequest.employeeId());
        Salary toCreate = Salary.builder()
                .employee(employee)
                .salary(createRequest.salary())
                .bonus(createRequest.bonus())
                .effectiveDate(createRequest.effectiveDate())
                .build();

        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.save(toCreate));
    }
}
