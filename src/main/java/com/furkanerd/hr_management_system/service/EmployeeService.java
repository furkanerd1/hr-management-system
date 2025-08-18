package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.UUID;

public interface EmployeeService {

    Employee getEmployeeEntityByEmail(String email);

    Employee getEmployeeEntityById(UUID id);

    boolean emailExists(String email);

    boolean phoneExists(String phone);
}
