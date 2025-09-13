package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.exception.CircularReferenceException;
import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.exception.PositionNotFoundException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.service.employee.EmployeeManagementService;
import com.furkanerd.hr_management_system.service.employee.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
class EmployeeManagementServiceImpl implements EmployeeManagementService {
    private final EmployeeService employeeCoreService;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    EmployeeManagementServiceImpl(EmployeeService employeeCoreService, DepartmentRepository departmentRepository, PositionRepository positionRepository, EmployeeMapper employeeMapper) {
        this.employeeCoreService = employeeCoreService;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    public EmployeeDetailResponse updateEmployee(UUID employeeIdToUpdate, EmployeeUpdateRequest updateRequest, String updatingUserEmail) {
        Employee updater = employeeCoreService.getEmployeeEntityByEmail(updatingUserEmail);
        Employee toUpdate = employeeCoreService.getEmployeeEntityById(employeeIdToUpdate);

        validateUpdatePermission(updater, toUpdate);
        Department department = findDepartment(updateRequest.departmentId());
        Position position = findPosition(updateRequest.positionId());
        Employee manager = validateAndGetManager(updateRequest.managerId(), toUpdate);
        updateStatusIfAllowed(updater, toUpdate, updateRequest);

        applyUpdates(toUpdate, updateRequest, department, position, manager);

        return employeeMapper.toEmployeeDetailResponse(employeeCoreService.saveEntity(toUpdate));
    }

    private void validateUpdatePermission(Employee updater, Employee toUpdate) {
        if (updater.getRole() == EmployeeRoleEnum.EMPLOYEE && !updater.getId().equals(toUpdate.getId())) {
            throw new UnauthorizedActionException("Employees can only update their own profile");
        }
    }

    private Department findDepartment(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }

    private Position findPosition(UUID positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));
    }

    private Employee validateAndGetManager(UUID managerId, Employee toUpdate) {
        if (managerId == null) return null;

        Employee manager = employeeCoreService.getEmployeeEntityById(managerId);
        if (isSubordinateOf(manager, toUpdate)) {
            throw new CircularReferenceException("Cannot assign manager. This would create a circular reporting hierarchy.");
        }
        return manager;
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


    private void updateStatusIfAllowed(Employee updater, Employee toUpdate, EmployeeUpdateRequest updateRequest) {
        if (updateRequest.status() == null) return;

        if (updater.getRole() == EmployeeRoleEnum.HR || updater.getRole() == EmployeeRoleEnum.MANAGER) {
            toUpdate.setStatus(updateRequest.status());
        } else {
            throw new UnauthorizedActionException("Only HR and Manager can update employee status");
        }
    }

    private void applyUpdates(Employee toUpdate, EmployeeUpdateRequest request, Department department, Position position, Employee manager) {
        toUpdate.setFirstName(request.firstName());
        toUpdate.setLastName(request.lastName());
        toUpdate.setPhone(request.phone());
        toUpdate.setAddress(request.address());
        toUpdate.setDepartment(department);
        toUpdate.setPosition(position);
        toUpdate.setManager(manager);
    }
}
