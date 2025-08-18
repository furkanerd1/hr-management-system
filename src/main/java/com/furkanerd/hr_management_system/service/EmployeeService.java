package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.UUID;

public interface EmployeeService {

    Employee getEmployeeByEmail(String email);

    Employee getEmployeeById(UUID id);
}
