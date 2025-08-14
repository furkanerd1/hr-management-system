package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Salary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalaryMapper {


    @Mapping(target = "employeeFullName", expression = "java(getFullName(salary))")
    @Mapping(target = "totalSalary", expression = "java(getTotalSalary(salary))")
    ListSalaryResponse salaryToListSalaryResponse(Salary salary);

    List<ListSalaryResponse> salariesToListSalaryResponses(List<Salary> salaries);


    @Mapping(target = "employeeFullName", expression = "java(getFullName(salary))")
    @Mapping(target = "totalSalary", expression = "java(getTotalSalary(salary))")
    @Mapping(target = "employeeId" , source = "salary.employee.id")
    @Mapping(target = "phone", source = "salary.employee.phone")
    @Mapping(target = "email", source = "salary.employee.email")
    @Mapping(target = "departmentName", source = "salary.employee.department.name")
    @Mapping(target = "positionName", source = "salary.employee.position.title")
    SalaryDetailResponse salaryToSalaryDetailResponse(Salary salary);


    default String getFullName(Salary salary) {
        return salary.getEmployee().getFirstName() + " " + salary.getEmployee().getLastName();
    }

    default BigDecimal getTotalSalary(Salary salary) {
        return salary.getSalary().add(salary.getBonus() != null ? salary.getBonus() : BigDecimal.ZERO);
    }
}
