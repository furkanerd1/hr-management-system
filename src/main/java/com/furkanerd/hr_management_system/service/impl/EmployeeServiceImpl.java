package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return employeeRepository.existsByPhone(phone);
    }
}
