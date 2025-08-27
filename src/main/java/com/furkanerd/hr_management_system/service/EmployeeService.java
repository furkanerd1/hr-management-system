package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDetailResponse getEmployeeDetailByEmail(String email);

    List<ListEmployeeResponse> listAllEmployees();

    EmployeeDetailResponse getEmployeeById(UUID id);

    EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate , EmployeeUpdateRequest updateRequest,String updatingUserEmail);

    Employee getEmployeeEntityByEmail(String email);

    Employee getEmployeeEntityById(UUID id);

    boolean emailExists(String email);

    boolean phoneExists(String phone);

    List<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId);

    List<ListPerformanceReviewResponse> getPerformanceReviewsByEmployeeId(UUID employeeId);

    List<ListAttendanceResponse> getAllAttendanceByEmployeeId(UUID id);
}
