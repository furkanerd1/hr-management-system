package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.service.AttendanceService;
import com.furkanerd.hr_management_system.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final EmployeeService employeeService;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, AttendanceMapper attendanceMapper, EmployeeService employeeService) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceMapper = attendanceMapper;
        this.employeeService = employeeService;
    }

    @Override
    public List<ListAttendanceResponse> listAllAttendance() {
        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAll());
    }

    @Override
    @Transactional
    public AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest) {
        Employee employee = employeeService.getEmployeeEntityById(createRequest.employeeId());

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
    public AttendanceDetailResponse autoCheckIn(String employeeEmail) {
        Employee employee = employeeService.getEmployeeEntityByEmail(employeeEmail);

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (!isValidCheckInTime(now)) {
            throw new InvalidAttendanceTimeException("Check-in time must be between 06:00 and 10:00");
        }

        if (attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), today)) {
            throw new AttendanceAlreadyExistsException(employee.getId(), today);
        }

        if (attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), today)) {
            throw new AttendanceAlreadyExistsException(employee.getId(), today);
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
    public AttendanceDetailResponse autoCheckOut(String employeeEmail) {
        Employee employee =  employeeService.getEmployeeEntityByEmail(employeeEmail);
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today)
                .orElseThrow(() -> new AttendanceNotFoundException("No attendance found for today " + employee.getId()));

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

    @Override
    public List<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail) {

        Employee employee = employeeService.getEmployeeEntityByEmail(employeeEmail);

        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAllByEmployeeId(employee.getId()));

    }

    private boolean isValidCheckInTime(LocalTime time) {
        return time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(10, 0));
    }
}
