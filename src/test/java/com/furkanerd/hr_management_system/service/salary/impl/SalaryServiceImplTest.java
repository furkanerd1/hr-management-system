package com.furkanerd.hr_management_system.service.salary.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.custom.ResourceNotFoundException;
import com.furkanerd.hr_management_system.mapper.SalaryMapper;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.SalaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryMapper salaryMapper;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    @Captor
    private ArgumentCaptor<Salary> salaryCaptor;

    @Test
    @DisplayName("Should list all salaries with pagination")
    void listAllSalaries_WhenValidRequest_ShouldReturnPaginatedResponse() {
        // given
        SalaryFilterRequest filterRequest = SalaryFilterRequest.empty();
        List<Salary> salaries = Arrays.asList(
                createTestSalary(),
                createTestSalary()
        );
        Page<Salary> salaryPage = new PageImpl<>(salaries);
        List<ListSalaryResponse> responses = Arrays.asList(
                ListSalaryResponse.builder().id(UUID.randomUUID()).build(),
                ListSalaryResponse.builder().id(UUID.randomUUID()).build()
        );

        given(salaryRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(salaryPage);
        given(salaryMapper.salariesToListSalaryResponses(salaries)).willReturn(responses);

        // when
        PaginatedResponse<ListSalaryResponse> result = salaryService
                .listAllSalaries(0, 10, "effectiveDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(2);
        assertThat(result.total()).isEqualTo(2);
        then(salaryRepository).should().findAll(nullable(Specification.class), any(Pageable.class));
        then(salaryMapper).should().salariesToListSalaryResponses(salaries);
    }

    @Test
    @DisplayName("Should get salary by id")
    void getSalaryById_WhenSalaryExists_ShouldReturnDetailResponse() {
        // given
        UUID salaryId = UUID.randomUUID();
        Salary salary = createTestSalary();
        SalaryDetailResponse expectedResponse = SalaryDetailResponse.builder()
                .id(salaryId)
                .salary(new BigDecimal("5000"))
                .bonus(new BigDecimal("500"))
                .build();

        given(salaryRepository.findById(salaryId)).willReturn(Optional.of(salary));
        given(salaryMapper.salaryToSalaryDetailResponse(salary)).willReturn(expectedResponse);

        // when
        SalaryDetailResponse result = salaryService.getSalaryById(salaryId);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(salaryRepository).should().findById(salaryId);
        then(salaryMapper).should().salaryToSalaryDetailResponse(salary);
    }

    @Test
    @DisplayName("Should throw exception when salary not found")
    void getSalaryById_WhenSalaryNotFound_ShouldThrowException() {
        // given
        UUID salaryId = UUID.randomUUID();
        given(salaryRepository.findById(salaryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.getSalaryById(salaryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Salary Not Found");
        then(salaryRepository).should().findById(salaryId);
    }

    @Test
    @DisplayName("Should create salary successfully")
    void createSalary_WhenValidRequest_ShouldCreateSalary() {
        // given
        UUID employeeId = UUID.randomUUID();
        SalaryCreateRequest request = new SalaryCreateRequest(
                employeeId,
                new BigDecimal("5000"),
                new BigDecimal("500"),
                LocalDate.now()
        );

        Employee employee = createTestEmployee(employeeId);

        Salary savedSalary = Salary.builder()
                .id(UUID.randomUUID())
                .employee(employee)
                .salary(request.salary())
                .bonus(request.bonus())
                .effectiveDate(request.effectiveDate())
                .build();

        SalaryDetailResponse expectedResponse = SalaryDetailResponse.builder()
                .id(savedSalary.getId())
                .salary(request.salary())
                .bonus(request.bonus())
                .build();

        given(employeeRepository.findById(employeeId)).willReturn(Optional.of(employee));
        given(salaryRepository.save(any(Salary.class))).willReturn(savedSalary);
        given(salaryMapper.salaryToSalaryDetailResponse(savedSalary)).willReturn(expectedResponse);

        // when
        SalaryDetailResponse result = salaryService.createSalary(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(salaryRepository).should().save(salaryCaptor.capture());
        Salary capturedSalary = salaryCaptor.getValue();
        assertThat(capturedSalary.getEmployee()).isEqualTo(employee);
        assertThat(capturedSalary.getSalary()).isEqualTo(request.salary());
        assertThat(capturedSalary.getBonus()).isEqualTo(request.bonus());
        assertThat(capturedSalary.getEffectiveDate()).isEqualTo(request.effectiveDate());
    }

    @Test
    @DisplayName("Should throw exception when employee not found for salary creation")
    void createSalary_WhenEmployeeNotFound_ShouldThrowException() {
        // given
        UUID employeeId = UUID.randomUUID();
        SalaryCreateRequest request = new SalaryCreateRequest(
                employeeId,
                new BigDecimal("5000"),
                new BigDecimal("500"),
                LocalDate.now()
        );

        given(employeeRepository.findById(employeeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> salaryService.createSalary(request))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().findById(employeeId);
    }

    @Test
    @DisplayName("Should show employee salary history")
    void showEmployeeSalaryHistory_WhenValidEmail_ShouldReturnPaginatedResponse() {
        // given
        String employeeEmail = "employee@company.com";
        SalaryFilterRequest filterRequest = SalaryFilterRequest.empty();
        List<Salary> salaries = Arrays.asList(createTestSalary());
        Page<Salary> salaryPage = new PageImpl<>(salaries);
        List<ListSalaryResponse> responses = Arrays.asList(
                ListSalaryResponse.builder().id(UUID.randomUUID()).build()
        );

        given(salaryRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(salaryPage);
        given(salaryMapper.salariesToListSalaryResponses(salaries)).willReturn(responses);

        // when
        PaginatedResponse<ListSalaryResponse> result = salaryService
                .showEmployeeSalaryHistory(employeeEmail, 0, 10, "effectiveDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(1);
        then(salaryRepository).should().findAll(nullable(Specification.class), any(Pageable.class));
        then(salaryMapper).should().salariesToListSalaryResponses(salaries);
    }

    @Test
    @DisplayName("Should delete salary successfully")
    void deleteSalary_WhenSalaryExists_ShouldDeleteSalary() {
        // given
        UUID salaryId = UUID.randomUUID();
        given(salaryRepository.existsById(salaryId)).willReturn(true);

        // when
        salaryService.deleteSalary(salaryId);

        // then
        then(salaryRepository).should().existsById(salaryId);
        then(salaryRepository).should().deleteById(salaryId);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existent salary")
    void deleteSalary_WhenSalaryNotFound_ShouldThrowException() {
        // given
        UUID salaryId = UUID.randomUUID();
        given(salaryRepository.existsById(salaryId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> salaryService.deleteSalary(salaryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Salary Not Found");
        then(salaryRepository).should().existsById(salaryId);
    }

    @Test
    @DisplayName("Should get salary history by employee id")
    void getSalaryHistoryByEmployee_WhenEmployeeExists_ShouldReturnPaginatedResponse() {
        // given
        UUID employeeId = UUID.randomUUID();
        SalaryFilterRequest filterRequest = SalaryFilterRequest.empty();
        List<Salary> salaries = Arrays.asList(createTestSalary());
        Page<Salary> salaryPage = new PageImpl<>(salaries);
        List<ListSalaryResponse> responses = Arrays.asList(
                ListSalaryResponse.builder().id(UUID.randomUUID()).build()
        );

        given(employeeRepository.existsById(employeeId)).willReturn(true);
        given(salaryRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(salaryPage);
        given(salaryMapper.salariesToListSalaryResponses(salaries)).willReturn(responses);

        // when
        PaginatedResponse<ListSalaryResponse> result = salaryService
                .getSalaryHistoryByEmployee(employeeId, 0, 10, "effectiveDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(1);
        then(employeeRepository).should().existsById(employeeId);
        then(salaryRepository).should().findAll(nullable(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found for salary history")
    void getSalaryHistoryByEmployee_WhenEmployeeNotFound_ShouldThrowException() {
        // given
        UUID employeeId = UUID.randomUUID();
        SalaryFilterRequest filterRequest = SalaryFilterRequest.empty();
        given(employeeRepository.existsById(employeeId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> salaryService
                .getSalaryHistoryByEmployee(employeeId, 0, 10, "effectiveDate", "desc", filterRequest))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().existsById(employeeId);
    }

    private Salary createTestSalary() {
        return Salary.builder()
                .id(UUID.randomUUID())
                .salary(new BigDecimal("5000"))
                .bonus(new BigDecimal("500"))
                .effectiveDate(LocalDate.now())
                .build();
    }

    private Employee createTestEmployee(UUID id) {
        return Employee.builder()
                .id(id)
                .email("employee@company.com")
                .firstName("John")
                .lastName("Doe")
                .build();
    }
}