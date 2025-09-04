package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.helper.EmployeeDomainService;
import com.furkanerd.hr_management_system.mapper.DepartmentMapper;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.request.employee.EmployeeFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.dto.response.employee.ListEmployeeResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.service.DepartmentService;
import com.furkanerd.hr_management_system.specification.DepartmentSpecification;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeDomainService employeeDomainService;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, EmployeeDomainService employeeDomainService) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.employeeDomainService = employeeDomainService;
    }


    @Override
    public PaginatedResponse<ListDepartmentResponse> listAllDepartments(int page,int size,String sortBy,String sortDirection,DepartmentFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate("department",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page,size,validatedSortBy,sortDirection);

        Specification<Department> specification = DepartmentSpecification.withFilters(filterRequest);

        Page<Department> departmentPage = departmentRepository.findAll(specification,pageable);
        List<ListDepartmentResponse> responseList = departmentMapper.departmentsToListDepartmentResponses(departmentPage.getContent());
        return PaginatedResponse.of(
                responseList,
                departmentPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public DepartmentDetailResponse getDepartmentById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        return departmentMapper.departmentToDepartmentDetailResponse(department);
    }

    @Override
    public PaginatedResponse<ListEmployeeResponse> getEmployeesByDepartment(UUID departmentId,int page,int size,String sortBy,String sortDirection, EmployeeFilterRequest filterRequest) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
        return employeeDomainService.getEmployeesByDepartmentId(departmentId,page,size,sortBy,sortDirection,filterRequest);
    }

    @Override
    @Transactional
    public DepartmentDetailResponse createDepartment(DepartmentCreateRequest createRequest) {
        Department toCreate = Department.builder()
                .name(createRequest.name())
                .description(createRequest.description())
                .build();
        return departmentMapper.departmentToDepartmentDetailResponse(departmentRepository.save(toCreate));

    }

    @Override
    @Transactional
    public DepartmentDetailResponse updateDepartment(UUID departmentId, DepartmentUpdateRequest updateRequest) {
        Department toUpdate = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
        toUpdate.setName(updateRequest.name());
        toUpdate.setDescription(updateRequest.description());
        return departmentMapper.departmentToDepartmentDetailResponse(departmentRepository.save(toUpdate));
    }

    @Override
    @Transactional
    public void deleteDepartment(UUID departmentId) {
       boolean exists = departmentRepository.existsById(departmentId);
       if (!exists) {
           throw new  DepartmentNotFoundException(departmentId);
       }
       departmentRepository.deleteById(departmentId);
    }

    @Override
    public Department getDepartmentEntityById(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }
}
