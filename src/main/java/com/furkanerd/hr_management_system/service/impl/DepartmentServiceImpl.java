package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.mapper.DepartmentMapper;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }


    @Override
    public List<ListDepartmentResponse> listAllDepartments() {

        return departmentMapper.departmentsToListDepartmentResponses(departmentRepository.findAll());
    }

    @Override
    public DepartmentDetailResponse getDepartmentById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        return departmentMapper.departmentToDepartmentDetailResponse(department);
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
}
