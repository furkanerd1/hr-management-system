package com.furkanerd.hr_management_system.service.attendance.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
import com.furkanerd.hr_management_system.exception.AttendanceNotFoundException;
import com.furkanerd.hr_management_system.exception.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.attendance.AttendanceQueryService;
import com.furkanerd.hr_management_system.specification.AttendanceSpecification;
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
@Transactional(readOnly = true)
class AttendanceQueryServiceImpl implements AttendanceQueryService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceQueryServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository, AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public PaginatedResponse<ListAttendanceResponse> listAllAttendance(int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.ATTENDANCE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Attendance> specification = AttendanceSpecification.withFilters(filterRequest);

        Page<Attendance> attendancePage = attendanceRepository.findAll(specification, pageable);
        List<ListAttendanceResponse> responseList = attendanceMapper.attendancesToListAttendanceResponse(attendancePage.getContent());
        return PaginatedResponse.of(
                responseList,
                attendancePage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public AttendanceDetailResponse getAttendanceById(UUID id) {
        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance with id: " + id))
        );
    }

    @Override
    public PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest) {
        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.ATTENDANCE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Attendance> baseSpec = AttendanceSpecification.withFilters(filterRequest);

        Specification<Attendance> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get(SortFieldConstants.EMPLOYEE_SORT_FIELD).get("id"), employee.getId()))
                : (root, query, cb) -> cb.equal(root.get(SortFieldConstants.EMPLOYEE_SORT_FIELD).get("id"), employee.getId());

        Page<Attendance> attendancePage = attendanceRepository.findAll(specification, pageable);
        List<ListAttendanceResponse> responseList = attendanceMapper.attendancesToListAttendanceResponse(attendancePage.getContent());

        return PaginatedResponse.of(
                responseList,
                attendancePage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public PaginatedResponse<ListAttendanceResponse> getAllAttendanceByEmployee(UUID id, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest) {
        boolean exists = employeeRepository.existsById(id);
        if (!exists) {
            throw new EmployeeNotFoundException(id);
        }

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.ATTENDANCE_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<Attendance> baseSpec = AttendanceSpecification.withFilters(filterRequest);

        Specification<Attendance> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("id"), id))
                : (root, query, cb) -> cb.equal(root.get("employee").get("id"), id);

        Page<Attendance> attendancePage = attendanceRepository.findAll(specification, pageable);
        List<ListAttendanceResponse> responseList = attendanceMapper.attendancesToListAttendanceResponse(attendancePage.getContent());

        return PaginatedResponse.of(
                responseList,
                attendancePage.getTotalElements(),
                page,
                size
        );
    }
}
