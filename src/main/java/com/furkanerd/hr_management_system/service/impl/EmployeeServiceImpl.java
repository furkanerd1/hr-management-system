package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.CircularReferenceException;
import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.*;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final SalaryService salaryService;
    private final PerformanceReviewService performanceReviewService;
    private final AttendanceService attendanceService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, DepartmentService departmentService, PositionService positionService, SalaryService salaryService, PerformanceReviewService performanceReviewService, AttendanceService attendanceService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.departmentService = departmentService;
        this.positionService = positionService;
        this.salaryService = salaryService;
        this.performanceReviewService = performanceReviewService;
        this.attendanceService = attendanceService;
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetailByEmail(String email) {
        Employee employee = getEmployeeEntityByEmail(email);        return employeeMapper.toEmployeeDetailResponse(employee);
    }

    @Override
    public PaginatedResponse<ListEmployeeResponse> listAllEmployees(int page, int size, String sortBy, String sortDirection) {

        Pageable pageable = PaginationUtils.buildPageable(page, size, sortBy, sortDirection);

        Page<Employee> employeePage = employeeRepository.findAll(pageable);
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
    public PaginatedResponse<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId,int page, int size,String sortedBy, String sortDirection) {
        boolean exists = employeeRepository.existsById(employeeId);
        if (!exists) {
            throw new EmployeeNotFoundException(employeeId);
        }
        return salaryService.getEmployeeSalaryHistory(employeeId,page,size,sortedBy,sortDirection);
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewsByEmployeeId(UUID employeeId,int page, int size, String sortBy, String sortDirection) {
        boolean exists = employeeRepository.existsById(employeeId);
         if (!exists) {
             throw new EmployeeNotFoundException(employeeId);
         }
         return performanceReviewService.getPerformanceReviewByEmployeeId(employeeId, page, size, sortBy, sortDirection);
    }

    @Override
    public PaginatedResponse<ListAttendanceResponse> getAllAttendanceByEmployeeId(UUID id,int  page, int size, String sortBy, String sortDirection) {
        boolean exists = employeeRepository.existsById(id);
        if (!exists) {
            throw new EmployeeNotFoundException(id);
        }
        return attendanceService.getAttendanceByEmployeeId(id,page,size,sortBy,sortDirection);
    }

    @Override
    public EmployeeLeaveBalanceResponse getLeaveBalance(UUID employeeId) {
        return employeeMapper.toEmployeeLeaveBalanceResponse(employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId)));
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

    @Override
    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    @Override
    public boolean phoneExists(String phone) {
        return employeeRepository.existsByPhone(phone);
    }


    /**
     * Checks whether the given subordinate is in the hierarchy of the manager.
     * This prevents the creation of a circular reference.
     *
     * @param subordinate The subordinate employee (potential new manager)
     * @param manager The employee whose manager is being changed
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
