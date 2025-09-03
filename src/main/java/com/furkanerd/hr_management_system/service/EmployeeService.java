package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDetailResponse getEmployeeDetailByEmail(String email);

    PaginatedResponse<ListEmployeeResponse> listAllEmployees(int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest);

    EmployeeDetailResponse getEmployeeById(UUID id);

    EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate , EmployeeUpdateRequest updateRequest,String updatingUserEmail);

    Employee getEmployeeEntityByEmail(String email);

    Employee getEmployeeEntityById(UUID id);

    boolean emailExists(String email);

    boolean phoneExists(String phone);

    PaginatedResponse<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId, int page, int size, String sortBy, String sortDirection, SalaryFilterRequest filterRequest);

    PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewsByEmployeeId(UUID employeeId, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest);

    PaginatedResponse<ListAttendanceResponse> getAllAttendanceByEmployeeId(UUID id, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest);

    EmployeeLeaveBalanceResponse getLeaveBalance(UUID employeeId);

    void saveEmployee(Employee employee);
}
