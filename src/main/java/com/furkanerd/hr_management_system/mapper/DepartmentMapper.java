package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {

    List<ListDepartmentResponse> departmentsToListDepartmentResponses(List<Department> departments);

    DepartmentDetailResponse departmentToDepartmentDetailResponse(Department department);
}
