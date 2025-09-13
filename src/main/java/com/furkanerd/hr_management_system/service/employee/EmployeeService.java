package com.furkanerd.hr_management_system.service.employee;

import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.UUID;

public interface EmployeeService {

    Employee getEmployeeEntityByEmail(String email);

    Employee getEmployeeEntityById(UUID id);

    EmployeeDetailResponse getEmployeeById(UUID id);

    EmployeeDetailResponse getEmployeeDetailByEmail(String email);

    Employee saveEntity(Employee employee);
}
