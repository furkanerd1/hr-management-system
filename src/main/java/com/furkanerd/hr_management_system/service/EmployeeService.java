package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.UUID;

public interface EmployeeService {

    EmployeeDetailResponse getEmployeeDetailByEmail(String email);

    PaginatedResponse<ListEmployeeResponse> listAllEmployees(int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest);

    EmployeeDetailResponse getEmployeeById(UUID id);

    EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate , EmployeeUpdateRequest updateRequest,String updatingUserEmail);

    Employee getEmployeeEntityByEmail(String email);

    Employee getEmployeeEntityById(UUID id);

    void saveEmployee(Employee employee);

    PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId,int page,int size,String sortBy,String sortDirection, EmployeeFilterRequest filterRequest);
}
