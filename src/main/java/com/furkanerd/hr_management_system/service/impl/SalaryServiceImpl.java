package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.ResourceNotFoundException;
import com.furkanerd.hr_management_system.helper.EmployeeDomainService;
import com.furkanerd.hr_management_system.mapper.SalaryMapper;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.repository.SalaryRepository;
import com.furkanerd.hr_management_system.service.SalaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final SalaryMapper salaryMapper;
    private final EmployeeDomainService employeeDomainService;

    public SalaryServiceImpl(SalaryRepository salaryRepository, SalaryMapper salaryMapper, EmployeeDomainService employeeDomainService) {
        this.salaryRepository = salaryRepository;
        this.salaryMapper = salaryMapper;
        this.employeeDomainService = employeeDomainService;
    }

    @Override
    public List<ListSalaryResponse> listAllSalaries() {
        return salaryMapper.salariesToListSalaryResponses( salaryRepository.findAll() );
    }

    @Override
    public SalaryDetailResponse getSalaryById(UUID id) {
        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Not Found")));
    }

    @Override
    @Transactional
    public SalaryDetailResponse createSalary(SalaryCreateRequest createRequest) {
        Employee employee =employeeDomainService.getEmployeeById(createRequest.employeeId());
        Salary toCreate = Salary.builder()
                .employee(employee)
                .salary(createRequest.salary())
                .bonus(createRequest.bonus())
                .effectiveDate(createRequest.effectiveDate())
                .build();

        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.save(toCreate));
    }

    @Override
    public List<ListSalaryResponse> showEmployeeSalaryHistory(String employeeEmail) {
        return salaryMapper.salariesToListSalaryResponses(salaryRepository.findAllByEmployeeEmail(employeeEmail));
    }

    @Override
    @Transactional
    public void deleteSalary(UUID id) {
        boolean exists = salaryRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Salary Not Found");
        }
        salaryRepository.deleteById(id);
    }

    @Override
    public List<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId) {
        return salaryMapper.salariesToListSalaryResponses(salaryRepository.findAllByEmployeeId(employeeId));
    }
}
