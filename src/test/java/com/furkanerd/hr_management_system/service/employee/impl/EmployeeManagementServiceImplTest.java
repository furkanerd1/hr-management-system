package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.service.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeManagementServiceImplTest {

    @Mock
    private EmployeeService employeeCoreService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeManagementServiceImpl employeeManagementService;

    private Employee hrEmployee;
    private Employee regularEmployee;
    private Employee managerEmployee;
    private Department department;
    private Position position;
    private EmployeeUpdateRequest updateRequest;
    private EmployeeDetailResponse employeeDetailResponse;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(UUID.randomUUID())
                .name("IT Department")
                .build();

        position = Position.builder()
                .id(UUID.randomUUID())
                .title("Software Developer")
                .build();

        hrEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("hr@company.com")
                .role(EmployeeRoleEnum.HR)
                .build();

        regularEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("employee@company.com")
                .role(EmployeeRoleEnum.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .build();

        managerEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("manager@company.com")
                .role(EmployeeRoleEnum.MANAGER)
                .build();

        updateRequest = new EmployeeUpdateRequest(
                "UpdatedFirstName",
                "UpdatedLastName",
                "123456789",
                "Updated Address",
                department.getId(),
                position.getId(),
                managerEmployee.getId(),
                EmployeeStatusEnum.ACTIVE
        );

        employeeDetailResponse = EmployeeDetailResponse.builder()
                .id(regularEmployee.getId())
                .fullName("UpdatedFirstName")
                .build();
    }

    @Test
    void updateEmployee_WithHRUpdatingEmployee_ShouldUpdateSuccessfully() {
        // Given
        when(employeeCoreService.getEmployeeEntityByEmail(hrEmployee.getEmail())).thenReturn(hrEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(employeeCoreService.getEmployeeEntityById(managerEmployee.getId())).thenReturn(managerEmployee);
        when(employeeCoreService.saveEntity(any(Employee.class))).thenReturn(regularEmployee);
        when(employeeMapper.toEmployeeDetailResponse(regularEmployee)).thenReturn(employeeDetailResponse);

        // When
        EmployeeDetailResponse result = employeeManagementService.updateEmployee(
                regularEmployee.getId(), updateRequest, hrEmployee.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(employeeDetailResponse.id(), result.id());
        assertEquals(employeeDetailResponse.fullName(), result.fullName());

        // Verify
        verify(employeeCoreService).getEmployeeEntityByEmail(hrEmployee.getEmail());
        verify(employeeCoreService).getEmployeeEntityById(regularEmployee.getId());
        verify(departmentRepository).findById(department.getId());
        verify(positionRepository).findById(position.getId());
        verify(employeeCoreService).saveEntity(regularEmployee);
        verify(employeeMapper).toEmployeeDetailResponse(regularEmployee);
    }

    @Test
    void updateEmployee_WithEmployeeUpdatingSelf_ShouldUpdateSuccessfully() {
        // Given
        when(employeeCoreService.getEmployeeEntityByEmail(regularEmployee.getEmail())).thenReturn(regularEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(employeeCoreService.getEmployeeEntityById(managerEmployee.getId())).thenReturn(managerEmployee);
        when(employeeCoreService.saveEntity(any(Employee.class))).thenReturn(regularEmployee);
        when(employeeMapper.toEmployeeDetailResponse(regularEmployee)).thenReturn(employeeDetailResponse);

        // Create update request without status change
        EmployeeUpdateRequest updateWithoutStatus = new EmployeeUpdateRequest(
                "UpdatedFirstName", "UpdatedLastName", "123456789", "Updated Address",
                department.getId(), position.getId(), managerEmployee.getId(), null
        );

        // When
        EmployeeDetailResponse result = employeeManagementService.updateEmployee(
                regularEmployee.getId(), updateWithoutStatus, regularEmployee.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(employeeDetailResponse.id(), result.id());

        // Verify
        verify(employeeCoreService).getEmployeeEntityByEmail(regularEmployee.getEmail());
        verify(employeeCoreService).getEmployeeEntityById(regularEmployee.getId());
        verify(employeeCoreService).saveEntity(regularEmployee);
    }

    @Test
    void updateEmployee_WithEmployeeUpdatingOthers_ShouldThrowUnauthorizedActionException() {
        // Given
        UUID otherEmployeeId = UUID.randomUUID();
        Employee otherEmployee = Employee.builder()
                .id(otherEmployeeId)
                .email("other@company.com")
                .role(EmployeeRoleEnum.EMPLOYEE)
                .build();

        when(employeeCoreService.getEmployeeEntityByEmail(regularEmployee.getEmail())).thenReturn(regularEmployee);
        when(employeeCoreService.getEmployeeEntityById(otherEmployeeId)).thenReturn(otherEmployee);

        // When & Then
        assertThrows(UnauthorizedActionException.class, () ->
                employeeManagementService.updateEmployee(otherEmployeeId, updateRequest, regularEmployee.getEmail()));

        // Verify
        verify(employeeCoreService).getEmployeeEntityByEmail(regularEmployee.getEmail());
        verify(employeeCoreService).getEmployeeEntityById(otherEmployeeId);
        verify(departmentRepository, never()).findById(any());
    }

    @Test
    void updateEmployee_WithInvalidDepartment_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID invalidDepartmentId = UUID.randomUUID();
        EmployeeUpdateRequest invalidRequest = new EmployeeUpdateRequest(
                "Name", "Last", "123", "Address", invalidDepartmentId, position.getId(), null, null
        );

        when(employeeCoreService.getEmployeeEntityByEmail(hrEmployee.getEmail())).thenReturn(hrEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(invalidDepartmentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DepartmentNotFoundException.class, () ->
                employeeManagementService.updateEmployee(regularEmployee.getId(), invalidRequest, hrEmployee.getEmail()));

        // Verify
        verify(departmentRepository).findById(invalidDepartmentId);
        verify(positionRepository, never()).findById(any());
    }

    @Test
    void updateEmployee_WithInvalidPosition_ShouldThrowPositionNotFoundException() {
        // Given
        UUID invalidPositionId = UUID.randomUUID();
        EmployeeUpdateRequest invalidRequest = new EmployeeUpdateRequest(
                "Name", "Last", "123", "Address", department.getId(), invalidPositionId, null, null
        );

        when(employeeCoreService.getEmployeeEntityByEmail(hrEmployee.getEmail())).thenReturn(hrEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(invalidPositionId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PositionNotFoundException.class, () ->
                employeeManagementService.updateEmployee(regularEmployee.getId(), invalidRequest, hrEmployee.getEmail()));

        // Verify
        verify(departmentRepository).findById(department.getId());
        verify(positionRepository).findById(invalidPositionId);
    }

    @Test
    void updateEmployee_WithCircularReference_ShouldThrowCircularReferenceException() {
        // Given
        Employee subordinate = Employee.builder()
                .id(UUID.randomUUID())
                .manager(regularEmployee) // regularEmployee is manager of subordinate
                .build();

        EmployeeUpdateRequest circularRequest = new EmployeeUpdateRequest(
                "Name", "Last", "123", "Address",
                department.getId(), position.getId(), subordinate.getId(), null
        );

        when(employeeCoreService.getEmployeeEntityByEmail(hrEmployee.getEmail())).thenReturn(hrEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(employeeCoreService.getEmployeeEntityById(subordinate.getId())).thenReturn(subordinate);

        // When & Then
        assertThrows(CircularReferenceException.class, () ->
                employeeManagementService.updateEmployee(regularEmployee.getId(), circularRequest, hrEmployee.getEmail()));

        // Verify
        verify(employeeCoreService).getEmployeeEntityById(subordinate.getId());
    }

    @Test
    void updateEmployee_WithEmployeeTryingToUpdateStatus_ShouldThrowUnauthorizedActionException() {
        // Given
        when(employeeCoreService.getEmployeeEntityByEmail(regularEmployee.getEmail())).thenReturn(regularEmployee);
        when(employeeCoreService.getEmployeeEntityById(regularEmployee.getId())).thenReturn(regularEmployee);
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));

        // When & Then
        assertThrows(UnauthorizedActionException.class, () ->
                employeeManagementService.updateEmployee(regularEmployee.getId(), updateRequest, regularEmployee.getEmail()));

        // Verify
        verify(employeeCoreService).getEmployeeEntityByEmail(regularEmployee.getEmail());
        verify(employeeCoreService).getEmployeeEntityById(regularEmployee.getId());
    }
}