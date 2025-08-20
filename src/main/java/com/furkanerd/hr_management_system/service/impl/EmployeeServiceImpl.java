package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.DepartmentService;
import com.furkanerd.hr_management_system.service.EmployeeService;
import com.furkanerd.hr_management_system.service.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, DepartmentService departmentService, PositionService positionService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetailByEmail(String email) {
        Employee employee = getEmployeeEntityByEmail(email);
        return employeeMapper.toEmployeeDetailResponse(employee);
    }

    @Override
    public List<ListEmployeeResponse> listAllEmployees() {
        return employeeMapper.employeestoListEmployeeResponseList(employeeRepository.findAll());
    }

    @Override
    public EmployeeDetailResponse getEmployeeById(UUID id) {
        return employeeMapper.toEmployeeDetailResponse(
                employeeRepository.findById(id)
                        .orElseThrow(() ->  new EmployeeNotFoundException(id))
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

        Department department =  departmentService.getDepartmentEntityById(updateRequest.departmentId());

        Position position =  positionService.getPositionEntityById(updateRequest.positionId());

        Employee manager = null;

        if(updateRequest.managerId() != null){
            manager=getEmployeeEntityById(updateRequest.managerId());
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
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException(email));
    }

    @Override
    public Employee getEmployeeEntityById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Override
    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return employeeRepository.existsByPhone(phone);
    }
}
