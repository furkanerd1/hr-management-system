package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    List<ListDepartmentResponse> listAllDepartments();

    DepartmentDetailResponse getDepartmentById(UUID id);

    List<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId);

    DepartmentDetailResponse createDepartment(DepartmentCreateRequest createRequest);

    DepartmentDetailResponse updateDepartment(UUID departmentId , DepartmentUpdateRequest updateRequest);

    void deleteDepartment(UUID departmentId);

    Department getDepartmentEntityById(UUID departmentId);
}
