package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.exception.custom.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeQueryServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeQueryServiceImpl employeeQueryService;

    private Employee employee;
    private Department department;
    private ListEmployeeResponse listEmployeeResponse;
    private EmployeeFilterRequest filterRequest;
    private Pageable pageable;
    private Page<Employee> employeePage;
    private UUID departmentId;

    @BeforeEach
    void setUp() {
        departmentId = UUID.randomUUID();

        department = Department.builder()
                .id(departmentId)
                .name("IT")
                .build();

        employee = Employee.builder()
                .id(UUID.randomUUID())
                .firstName("Furkan")
                .lastName("Erd")
                .email("furkan@example.com")
                .department(department)
                .build();

        listEmployeeResponse = ListEmployeeResponse.builder()
                .id(employee.getId())
                .fullName("Furkan Erd")
                .email("furkan@example.com")
                .build();

        filterRequest = EmployeeFilterRequest.empty();

        int page = 0;
        int size = 10;
        String sortBy = "firstName";
        String sortDirection = "ASC";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        pageable = PageRequest.of(page, size, sort);

        employeePage = new PageImpl<>(List.of(employee), pageable, 1);
    }


    @Test
    void listAllEmployees_ShouldReturnPaginatedListOfEmployees() {
        // Given
        when(employeeRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(employeePage);
        when(employeeMapper.employeestoListEmployeeResponseList(List.of(employee))).thenReturn(List.of(listEmployeeResponse));

        // When -
        PaginatedResponse<ListEmployeeResponse> result = employeeQueryService.listAllEmployees(0, 10, "firstName", "ASC", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(0, result.page());
        assertEquals(1, result.data().size());
        assertEquals(listEmployeeResponse.id(), result.data().getFirst().id());

        // Verify
        verify(employeeRepository).findAll(nullable(Specification.class), any(Pageable.class));
        verify(employeeMapper).employeestoListEmployeeResponseList(List.of(employee));
    }

    @Test
    void listAllEmployees_WhenNoEmployeesFound_ShouldReturnEmptyPaginatedResponse() {
        // Given
        Page<Employee> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(employeeRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(emptyPage);
        when(employeeMapper.employeestoListEmployeeResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        PaginatedResponse<ListEmployeeResponse> result = employeeQueryService.listAllEmployees(0, 10, "firstName", "ASC", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(0, result.total());
        assertEquals(0, result.page());
        assertTrue(result.data().isEmpty());

        // Verify
        verify(employeeRepository).findAll(nullable(Specification.class), any(Pageable.class));
        verify(employeeMapper).employeestoListEmployeeResponseList(Collections.emptyList());
    }



    @Test
    void getEmployeesByDepartment_WhenDepartmentExists_ShouldReturnPaginatedListOfEmployees() {
        // Given
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(employeeRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(employeePage);
        when(employeeMapper.employeestoListEmployeeResponseList(List.of(employee))).thenReturn(List.of(listEmployeeResponse));

        // When
        PaginatedResponse<ListEmployeeResponse> result = employeeQueryService.getEmployeesByDepartment(departmentId, 0, 10, "firstName", "ASC", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals(listEmployeeResponse.id(), result.data().getFirst().id());
        assertEquals(listEmployeeResponse.fullName(), result.data().getFirst().fullName());

        // Verify
        verify(departmentRepository).findById(departmentId);
        verify(employeeRepository).findAll(nullable(Specification.class), any(Pageable.class));
        verify(employeeMapper).employeestoListEmployeeResponseList(List.of(employee));
    }

    @Test
    void getEmployeesByDepartment_WhenDepartmentNotFound_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID nonExistentDepartmentId = UUID.randomUUID();
        when(departmentRepository.findById(nonExistentDepartmentId)).thenReturn(Optional.empty());

        // When & Then
        DepartmentNotFoundException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> employeeQueryService.getEmployeesByDepartment(
                        nonExistentDepartmentId, 0, 10, "id", "ASC", filterRequest
                )
        );

        String expectedMessage = String.format("Department not found with identifier: %s", nonExistentDepartmentId);
        assertEquals(expectedMessage, exception.getMessage());

        // Verify
        verify(departmentRepository).findById(nonExistentDepartmentId);
        verify(employeeRepository, never()).findAll(nullable(Specification.class), any(Pageable.class));

    }
}