package com.furkanerd.hr_management_system.service.employee.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.EmployeeMapper;
import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    @DisplayName("Should return employee entity when found by email")
    void getEmployeeEntityByEmail_WhenEmployeeExists_ShouldReturnEmployee() {
        // given
        String email = "john.doe@company.com";
        Employee expectedEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .build();
        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(expectedEmployee));

        // when
        Employee result = employeeService.getEmployeeEntityByEmail(email);

        // then
        assertThat(result).isEqualTo(expectedEmployee);
        then(employeeRepository).should().findByEmail(email);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when employee not found by email")
    void getEmployeeEntityByEmail_WhenEmployeeNotExists_ShouldThrowException() {
        // given
        String email = "nonexistent@company.com";
        given(employeeRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> employeeService.getEmployeeEntityByEmail(email))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().findByEmail(email);
    }

    @Test
    @DisplayName("Should return employee entity when found by id")
    void getEmployeeEntityById_WhenEmployeeExists_ShouldReturnEmployee() {
        // given
        UUID employeeId = UUID.randomUUID();
        Employee expectedEmployee = Employee.builder()
                .id(employeeId)
                .email("john.doe@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        given(employeeRepository.findById(employeeId)).willReturn(Optional.of(expectedEmployee));

        // when
        Employee result = employeeService.getEmployeeEntityById(employeeId);

        // then
        assertThat(result).isEqualTo(expectedEmployee);
        then(employeeRepository).should().findById(employeeId);
    }

    @Test
    @DisplayName("Should throw EmployeeNotFoundException when employee not found by id")
    void getEmployeeEntityById_WhenEmployeeNotExists_ShouldThrowException() {
        // given
        UUID employeeId = UUID.randomUUID();
        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> employeeService.getEmployeeEntityById(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().findById(employeeId);
    }

    @Test
    @DisplayName("Should return employee detail response when found by id")
    void getEmployeeById_WhenEmployeeExists_ShouldReturnDetailResponse() {
        // given
        UUID employeeId = UUID.randomUUID();
        Employee employee = Employee.builder()
                .id(employeeId)
                .email("john.doe@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        EmployeeDetailResponse expectedResponse = EmployeeDetailResponse.builder()
                .id(employeeId)
                .email("john.doe@company.com")
                .fullName("John Doe")
                .build();

        given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));
        given(employeeMapper.toEmployeeDetailResponse(employee)).willReturn(expectedResponse);

        // when
        EmployeeDetailResponse result = employeeService.getEmployeeById(employeeId);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(employeeRepository).should().findById(employeeId);
        then(employeeMapper).should().toEmployeeDetailResponse(employee);
    }

    @Test
    @DisplayName("Should return employee detail response when found by email")
    void getEmployeeDetailByEmail_WhenEmployeeExists_ShouldReturnDetailResponse() {
        // given
        String email = "john.doe@company.com";
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .build();
        EmployeeDetailResponse expectedResponse = EmployeeDetailResponse.builder()
                .id(employee.getId())
                .email(email)
                .fullName("John Doe")
                .build();

        given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
        given(employeeMapper.toEmployeeDetailResponse(employee)).willReturn(expectedResponse);

        // when
        EmployeeDetailResponse result = employeeService.getEmployeeDetailByEmail(email);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(employeeRepository).should().findByEmail(email);
        then(employeeMapper).should().toEmployeeDetailResponse(employee);
    }

    @Test
    @DisplayName("Should save and return employee entity")
    void saveEntity_ShouldSaveAndReturnEmployee() {
        // given
        Employee employeeToSave = Employee.builder()
                .email("john.doe@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        Employee savedEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .email("john.doe@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        given(employeeRepository.save(employeeToSave)).willReturn(savedEmployee);

        // when
        Employee result = employeeService.saveEntity(employeeToSave);

        // then
        assertThat(result).isEqualTo(savedEmployee);
        then(employeeRepository).should().save(employeeToSave);
    }
}