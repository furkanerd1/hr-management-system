package com.furkanerd.hr_management_system.service.salary.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.ResourceNotFoundException;
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
import com.furkanerd.hr_management_system.service.salary.SalaryService;
import com.furkanerd.hr_management_system.specification.SalarySpecification;
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
class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryMapper salaryMapper;

    public SalaryServiceImpl(SalaryRepository salaryRepository, SalaryMapper salaryMapper,EmployeeRepository employeeRepository) {
        this.salaryRepository = salaryRepository;
        this.salaryMapper = salaryMapper;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public PaginatedResponse<ListSalaryResponse> listAllSalaries(int page, int size, String sortBy, String sortDirection, SalaryFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.SALARY_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Salary> specification = SalarySpecification.withFilters(filterRequest);

        Page<Salary> salaryPage = salaryRepository.findAll(specification, pageable);
        List<ListSalaryResponse> responseList = salaryMapper.salariesToListSalaryResponses(salaryPage.getContent());

        return PaginatedResponse.of(
                responseList,
                salaryPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public SalaryDetailResponse getSalaryById(UUID id) {
        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary Not Found")));
    }

    @Override
    @Transactional
    public SalaryDetailResponse createSalary(SalaryCreateRequest createRequest) {
        Employee employee = employeeRepository.findById(createRequest.employeeId()).orElseThrow(() -> new EmployeeNotFoundException(createRequest.employeeId()));
        Salary toCreate = Salary.builder()
                .employee(employee)
                .salary(createRequest.salary())
                .bonus(createRequest.bonus())
                .effectiveDate(createRequest.effectiveDate())
                .build();

        return salaryMapper.salaryToSalaryDetailResponse(salaryRepository.save(toCreate));
    }

    @Override
    public PaginatedResponse<ListSalaryResponse> showEmployeeSalaryHistory(String employeeEmail, int page, int size, String sortBy, String sortDirection, SalaryFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.SALARY_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Salary> baseSpec = SalarySpecification.withFilters(filterRequest);

        Specification<Salary> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("email"), employeeEmail))
                : (root, query, cb) -> cb.equal(root.get("employee").get("email"), employeeEmail);


        Page<Salary> salaryPage = salaryRepository.findAll(specification, pageable);
        List<ListSalaryResponse> responseList = salaryMapper.salariesToListSalaryResponses(salaryPage.getContent());

        return PaginatedResponse.of(
                responseList,
                salaryPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    @Transactional
    public void deleteSalary(UUID id) {
        boolean exists = salaryRepository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Salary Not Found");
        }
        salaryRepository.deleteById(id);
    }

    @Override
    public PaginatedResponse<ListSalaryResponse> getSalaryHistoryByEmployee(UUID employeeId, int page, int size, String sortBy, String sortDirection, SalaryFilterRequest filterRequest) {
        boolean exists = employeeRepository.existsById(employeeId);
        if (!exists) {
            throw new EmployeeNotFoundException(employeeId);
        }

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.SALARY_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Salary> baseSpec = SalarySpecification.withFilters(filterRequest);

        Specification<Salary> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId))
                : (root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId);

        Page<Salary> salaryPage = salaryRepository.findAll(specification, pageable);
        List<ListSalaryResponse> responseList = salaryMapper.salariesToListSalaryResponses(salaryPage.getContent());

        return PaginatedResponse.of(
                responseList,
                salaryPage.getTotalElements(),
                page,
                size
        );
    }
}
