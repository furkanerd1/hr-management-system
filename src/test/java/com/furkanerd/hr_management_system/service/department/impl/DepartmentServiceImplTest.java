package com.furkanerd.hr_management_system.service.department.impl;

import com.furkanerd.hr_management_system.exception.custom.DepartmentNotFoundException;
import com.furkanerd.hr_management_system.mapper.DepartmentMapper;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.department.DepartmentUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.DepartmentDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.department.ListDepartmentResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentDetailResponse departmentDetailResponse;
    private ListDepartmentResponse listDepartmentResponse;
    private DepartmentCreateRequest createRequest;
    private DepartmentUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(UUID.randomUUID())
                .name("IT Department")
                .description("Information Technology Department")
                .build();

        departmentDetailResponse = DepartmentDetailResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();

        listDepartmentResponse = ListDepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();

        createRequest = new DepartmentCreateRequest("IT Department", "Information Technology Department");
        updateRequest = new DepartmentUpdateRequest("Updated IT Department", "Updated Description");
    }

    @Test
    void listAllDepartments_WithValidParameters_ShouldReturnPaginatedResponse() {
        // Given
        DepartmentFilterRequest filterRequest = DepartmentFilterRequest.empty();
        List<Department> departments = Arrays.asList(department);
        Page<Department> departmentPage = new PageImpl<>(departments, Pageable.unpaged(), departments.size());
        List<ListDepartmentResponse> responses = Arrays.asList(listDepartmentResponse);

        when(departmentRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(departmentPage);
        when(departmentMapper.departmentsToListDepartmentResponses(departments)).thenReturn(responses);

        // When
        PaginatedResponse<ListDepartmentResponse> result = departmentService.listAllDepartments(
                0, 10, "name", "asc", filterRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.total());
        assertEquals(1, result.data().size());
        assertEquals(listDepartmentResponse.id(), result.data().get(0).id());

        // Verify
        verify(departmentRepository).findAll(nullable(Specification.class), any(Pageable.class));
        verify(departmentMapper).departmentsToListDepartmentResponses(departments);
    }

    @Test
    void getDepartmentById_WithValidId_ShouldReturnDepartmentDetailResponse() {
        // Given
        UUID departmentId = department.getId();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(departmentMapper.departmentToDepartmentDetailResponse(department)).thenReturn(departmentDetailResponse);

        // When
        DepartmentDetailResponse result = departmentService.getDepartmentById(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(departmentDetailResponse.id(), result.id());
        assertEquals(departmentDetailResponse.name(), result.name());

        // Verify
        verify(departmentRepository).findById(departmentId);
        verify(departmentMapper).departmentToDepartmentDetailResponse(department);
    }

    @Test
    void getDepartmentById_WithInvalidId_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(departmentRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DepartmentNotFoundException.class, () ->
                departmentService.getDepartmentById(invalidId));

        // Verify
        verify(departmentRepository).findById(invalidId);
        verify(departmentMapper, never()).departmentToDepartmentDetailResponse(any());
    }

    @Test
    void createDepartment_WithValidData_ShouldCreateDepartmentSuccessfully() {
        // Given
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.departmentToDepartmentDetailResponse(department)).thenReturn(departmentDetailResponse);

        // When
        DepartmentDetailResponse result = departmentService.createDepartment(createRequest);

        // Then
        assertNotNull(result);
        assertEquals(departmentDetailResponse.id(), result.id());
        assertEquals(departmentDetailResponse.name(), result.name());

        // Verify
        verify(departmentRepository).save(any(Department.class));
        verify(departmentMapper).departmentToDepartmentDetailResponse(department);
    }

    @Test
    void updateDepartment_WithValidData_ShouldUpdateDepartmentSuccessfully() {
        // Given
        UUID departmentId = department.getId();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.departmentToDepartmentDetailResponse(department)).thenReturn(departmentDetailResponse);

        // When
        DepartmentDetailResponse result = departmentService.updateDepartment(departmentId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(departmentDetailResponse.id(), result.id());

        // Verify
        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository).save(department);
        verify(departmentMapper).departmentToDepartmentDetailResponse(department);
    }

    @Test
    void updateDepartment_WithInvalidId_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(departmentRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DepartmentNotFoundException.class, () ->
                departmentService.updateDepartment(invalidId, updateRequest));

        // Verify
        verify(departmentRepository).findById(invalidId);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartment_WithValidId_ShouldDeleteDepartmentSuccessfully() {
        // Given
        UUID departmentId = department.getId();
        when(departmentRepository.existsById(departmentId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> departmentService.deleteDepartment(departmentId));

        // Then & Verify
        verify(departmentRepository).existsById(departmentId);
        verify(departmentRepository).deleteById(departmentId);
    }

    @Test
    void deleteDepartment_WithInvalidId_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(departmentRepository.existsById(invalidId)).thenReturn(false);

        // When & Then
        assertThrows(DepartmentNotFoundException.class, () ->
                departmentService.deleteDepartment(invalidId));

        // Verify
        verify(departmentRepository).existsById(invalidId);
        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void getDepartmentEntityById_WithValidId_ShouldReturnDepartmentEntity() {
        // Given
        UUID departmentId = department.getId();
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // When
        Department result = departmentService.getDepartmentEntityById(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(department.getId(), result.getId());
        assertEquals(department.getName(), result.getName());

        // Verify
        verify(departmentRepository).findById(departmentId);
    }

    @Test
    void getDepartmentEntityById_WithInvalidId_ShouldThrowDepartmentNotFoundException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(departmentRepository.findById(invalidId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DepartmentNotFoundException.class, () ->
                departmentService.getDepartmentEntityById(invalidId));

        // Verify
        verify(departmentRepository).findById(invalidId);
    }
}