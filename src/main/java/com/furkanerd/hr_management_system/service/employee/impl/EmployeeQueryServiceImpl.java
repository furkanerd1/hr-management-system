package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.employee.EmployeeQueryService;
import com.furkanerd.hr_management_system.specification.EmployeeSpecification;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
class EmployeeQueryServiceImpl implements EmployeeQueryService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeQueryServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public PaginatedResponse<ListEmployeeResponse> listAllEmployees(int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.EMPLOYEE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Employee> specification = EmployeeSpecification.withFilters(filterRequest);

        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);
        List<ListEmployeeResponse> responseList = employeeMapper.employeestoListEmployeeResponseList(employeePage.getContent());

        return PaginatedResponse.of(
                responseList,
                employeePage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId, int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.EMPLOYEE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Employee> specification = EmployeeSpecification.withFilters(filterRequest);
        Specification<Employee> departmentIdSpec =
                (root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId);

        Specification<Employee> combinedSpec = (specification != null)
                ? specification.and(departmentIdSpec)
                : departmentIdSpec;

        Page<Employee> employeePage = employeeRepository.findAll(combinedSpec, pageable);
        List<ListEmployeeResponse> responseList = employeeMapper.employeestoListEmployeeResponseList(employeePage.getContent());

        return PaginatedResponse.of(
                responseList,
                employeePage.getTotalElements(),
                page,
                size
        );
    }
}
