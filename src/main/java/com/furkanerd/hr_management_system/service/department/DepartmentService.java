package com.furkanerd.hr_management_system.service.department;

import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.entity.Department;

import java.util.UUID;

public interface DepartmentService {

    PaginatedResponse<ListDepartmentResponse> listAllDepartments(int page, int size,String sortBy,String sortDirection, DepartmentFilterRequest filterRequest);

    DepartmentDetailResponse getDepartmentById(UUID id);

    DepartmentDetailResponse createDepartment(DepartmentCreateRequest createRequest);

    DepartmentDetailResponse updateDepartment(UUID departmentId , DepartmentUpdateRequest updateRequest);

    void deleteDepartment(UUID departmentId);

    Department getDepartmentEntityById(UUID departmentId);
}
