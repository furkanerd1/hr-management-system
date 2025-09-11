package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
import com.furkanerd.hr_management_system.exception.CircularReferenceException;
import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.service.*;
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
class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper,PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
        this.positionRepository = positionRepository;
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetailByEmail(String email) {
        Employee employee = getEmployeeEntityByEmail(email);
        return employeeMapper.toEmployeeDetailResponse(employee);
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
    public EmployeeDetailResponse getEmployeeById(UUID id) {
        return employeeMapper.toEmployeeDetailResponse(
                employeeRepository.findById(id)
                        .orElseThrow(() -> new EmployeeNotFoundException(id))
        );
    }

    @Override
    @Transactional
    public EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate, EmployeeUpdateRequest updateRequest, String updatingUserEmail) {

        Employee updater = getEmployeeEntityByEmail(updatingUserEmail);

        Employee toUpdate = getEmployeeEntityById(employeeIdToUpdate);

        if (updater.getRole() == EmployeeRoleEnum.EMPLOYEE && !updater.getId().equals(toUpdate.getId())) {
            throw new UnauthorizedActionException("Employees can only update their own profile");
        }

        Department department =departmentRepository.findById(updateRequest.departmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(updateRequest.departmentId()));
        Position position = positionRepository.findById(updateRequest.positionId())
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + updateRequest.positionId()));

        Employee manager = null;

        if (updateRequest.managerId() != null) {
            manager = getEmployeeEntityById(updateRequest.managerId());

            //  circular reference control
            if (isSubordinateOf(manager, toUpdate)) {
                throw new CircularReferenceException("Cannot assign manager. This would create a circular reporting hierarchy.");
            }
        }

        if (updateRequest.status() != null) {
            if (updater.getRole() == EmployeeRoleEnum.HR || updater.getRole() == EmployeeRoleEnum.MANAGER) {
                toUpdate.setStatus(updateRequest.status());
            } else {
                throw new UnauthorizedActionException("Only HR and Manager can update employee status");
            }
        }

        toUpdate.setFirstName(updateRequest.firstName());
        toUpdate.setLastName(updateRequest.lastName());
        toUpdate.setPhone(updateRequest.phone());
        toUpdate.setAddress(updateRequest.address());
        toUpdate.setDepartment(department);
        toUpdate.setPosition(position);
        toUpdate.setManager(manager);


        return employeeMapper.toEmployeeDetailResponse(employeeRepository.save(toUpdate));
    }

    @Override
    public PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId, int page, int size, String sortBy, String sortDirection, EmployeeFilterRequest filterRequest) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.EMPLOYEE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Employee> specification = EmployeeSpecification.withFilters(filterRequest);
        Specification<Employee> departmentIdSpec = (root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId);

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

    @Override
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    @Override
    public Employee getEmployeeEntityById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }


    /**
     * Checks whether the given subordinate is in the hierarchy of the manager.
     * This prevents the creation of a circular reference.
     *
     * @param subordinate The subordinate employee (potential new manager)
     * @param manager     The employee whose manager is being changed
     * @return true if the subordinate appears above in the manager's hierarchy, false otherwise
     */
    private boolean isSubordinateOf(Employee subordinate, Employee manager) {
        if (subordinate == null || manager == null) {
            return false;
        }

        if (subordinate.getId().equals(manager.getId())) {
            return true;
        }

        Employee currentManager = subordinate.getManager();
        while (currentManager != null) {
            if (currentManager.getId().equals(manager.getId())) {
                return true;
            }
            currentManager = currentManager.getManager();
        }
        return false;
    }
}
