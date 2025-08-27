package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.helper.EmployeeDomainService;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
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
    private final AttendanceMapper attendanceMapper;
    private final EmployeeDomainService employeeDomainService;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, AttendanceMapper attendanceMapper, EmployeeDomainService employeeDomainService) {
        this.attendanceRepository = attendanceRepository;
        this.attendanceMapper = attendanceMapper;
        this.employeeDomainService = employeeDomainService;
    }

    @Override
    public List<ListAttendanceResponse> listAllAttendance() {
        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAll());
    }

    @Override
    public AttendanceDetailResponse getAttendanceById(UUID id) {
        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance with id: " + id))
        );
    }

    @Override
    @Transactional
    public AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest) {
        Employee employee = employeeDomainService.getEmployeeById(createRequest.employeeId());

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
        Employee employee = employeeDomainService.getEmployeeByEmail(employeeEmail);

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
        Employee employee =  employeeDomainService.getEmployeeByEmail(employeeEmail);
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
    @Transactional
    public AttendanceDetailResponse updateAttendance(UUID id, AttendanceUpdateRequest updateRequest) {

        Attendance toUpdateAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance with id: " + id));

        if (updateRequest.checkInTime() != null && updateRequest.checkOutTime() != null) {
            if (updateRequest.checkInTime().isAfter(updateRequest.checkOutTime())) {
                throw new InvalidAttendanceTimeException("Check-out time must be after check-in time");
            }
        }

        if (updateRequest.checkInTime() != null) {
            toUpdateAttendance.setCheckInTime(updateRequest.checkInTime());
        }

        if (updateRequest.checkOutTime() != null) {
            toUpdateAttendance.setCheckOutTime(updateRequest.checkOutTime());
        }
        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(toUpdateAttendance));
    }

    @Override
    public List<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail) {

        Employee employee = employeeDomainService.getEmployeeByEmail(employeeEmail);

        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAllByEmployeeId(employee.getId()));

    }

    @Override
    public void deleteAttendance(UUID id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("No attendance found for id " + id));
        attendanceRepository.delete(attendance);
    }

    @Override
    public List<ListAttendanceResponse> getAttendanceByEmployeeId(UUID id) {
        return attendanceMapper.attendancesToListAttendanceResponse(attendanceRepository.findAllByEmployeeId(id));
    }

    private boolean isValidCheckInTime(LocalTime time) {
        return time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(10, 0));
    }
}
