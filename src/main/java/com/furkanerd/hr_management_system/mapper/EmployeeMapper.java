package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    @Mapping(target = "fullName",expression = "java(getEmployeeFullName(employee))")
    @Mapping(target = "departmentName",source = "employee.department.name")
    @Mapping(target = "positionTitle",source = "employee.position.title")
    ListEmployeeResponse toListEmployeeResponse(Employee employee);

    List<ListEmployeeResponse> employeestoListEmployeeResponseList(List<Employee> employees);

    @Mapping(target = "fullName",expression = "java(getEmployeeFullName(employee))")
    @Mapping(target = "departmentName",source = "employee.department.name")
    @Mapping(target = "positionTitle",source = "employee.position.title")
    @Mapping(target = "managerFullName",expression = "java(getManagerFullName(employee))")
    EmployeeDetailResponse  toEmployeeDetailResponse(Employee employee);

    default String getEmployeeFullName(Employee employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }

    default String getManagerFullName(Employee employee) {
        if (employee.getManager() == null) {
            return "No Manager";
        }
        return employee.getManager().getFirstName() + " " + employee.getManager().getLastName();
    }
}
