package com.furkanerd.hr_management_system.service.employee;


import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;

import java.util.UUID;

public interface EmployeeManagementService {

    EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate, EmployeeUpdateRequest updateRequest, String updatingUserEmail);
}
