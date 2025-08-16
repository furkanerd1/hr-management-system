package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository, AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public List<ListAttendanceResponse> listAllAttendance() {
        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAll());
    }

    @Override
    @Transactional
    public AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest) {
        Employee employee = employeeRepository.findById(createRequest.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(createRequest.employeeId()));

        if (attendanceRepository.existsByEmployeeIdAndDate(createRequest.employeeId(),createRequest.date())){
            throw new AttendanceAlreadyExistsException(createRequest.employeeId(), createRequest.date());
        }
        if (createRequest.checkOutTime() != null && !createRequest.checkOutTime().isAfter(createRequest.checkInTime())) {
            throw new InvalidAttendanceTimeException("Check-out time must be after check-in time");
        }

        Attendance attendance= Attendance.builder()
                .employee(employee)
                .date(createRequest.date())
                .checkInTime(createRequest.checkInTime())
                .checkOutTime(createRequest.checkOutTime())
                .build();

        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(attendance));

    }

    @Override
    @Transactional
    public AttendanceDetailResponse autoCheckIn(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (attendanceRepository.existsByEmployeeIdAndDate(employeeId, today)) {
            throw new AttendanceAlreadyExistsException(employeeId, today);
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(today)
                .checkInTime(now)
                .build();

        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(attendance));
    }


    @Override
    @Transactional
    public AttendanceDetailResponse autoCheckOut(UUID employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new AttendanceNotFoundException("No attendance found for today " + employeeId));

        LocalTime now = LocalTime.now();

        if (attendance.getCheckOutTime() != null) {
            throw new AlreadyCheckedOutException("Already checked out today");
        }

        if (!now.isAfter(attendance.getCheckInTime())) {
            throw new InvalidAttendanceTimeException("Check-out must be after check-in");
        }

        attendance.setCheckOutTime(now);
        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(attendance));

    }
}
