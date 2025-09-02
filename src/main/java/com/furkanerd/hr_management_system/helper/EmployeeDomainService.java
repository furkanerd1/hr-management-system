package com.furkanerd.hr_management_system.helper;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeDomainService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDomainService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public Employee getEmployeeById(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    public PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartmentId(UUID departmentId,int page,int size,String sortBy,String sortDirection) {
        String validatedSortBy = validateSortField(sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page,size,validatedSortBy,sortDirection);

        Page<Employee> employeePage = employeeRepository.findAllByDepartmentId(departmentId,pageable);
        List<ListEmployeeResponse> responseList = employeeMapper.employeestoListEmployeeResponseList(employeePage.getContent());

        return PaginatedResponse.of(
                responseList,
                employeePage.getTotalElements(),
                page,
                size
        );
    }

    /**
     * Validates the sortBy field to ensure it matches allowed fields for sorting.
     * This prevents malicious input that could manipulate the generated SQL query.
     */
    private String validateSortField(String sortBy) {
        List<String> validFields = List.of("id", "firstName", "lastName", "email", "phone", "hireDate","birthDate","address", "status", "role","createdAt", "updatedAt");
        return validFields.contains(sortBy) ? sortBy : "firstName";
    }
}
