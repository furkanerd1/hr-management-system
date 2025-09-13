package com.furkanerd.hr_management_system.service.employee;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;

import java.util.UUID;

public interface EmployeeQueryService {

    PaginatedResponse<ListEmployeeResponse> listAllEmployees(int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest);

    PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId, int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest);

}
