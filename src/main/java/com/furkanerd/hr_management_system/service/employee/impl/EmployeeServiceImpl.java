package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.employee.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    @Override
    public Employee getEmployeeEntityById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    public EmployeeDetailResponse getEmployeeById(UUID id) {
        return employeeMapper.toEmployeeDetailResponse(
                employeeRepository.findById(id)
                        .orElseThrow(() -> new EmployeeNotFoundException(id))
        );
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetailByEmail(String email) {
        Employee employee = getEmployeeEntityByEmail(email);
        return employeeMapper.toEmployeeDetailResponse(employee);
    }

    @Override
    public Employee saveEntity(Employee employee) {
        return employeeRepository.save(employee);
    }
}



