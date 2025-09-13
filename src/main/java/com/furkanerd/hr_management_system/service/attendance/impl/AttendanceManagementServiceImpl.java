package com.furkanerd.hr_management_system.service.attendance.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.mapper.AttendanceMapper;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.repository.AttendanceRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.attendance.AttendanceManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;


@Service
@Transactional
class AttendanceManagementServiceImpl implements AttendanceManagementService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    private static final Duration MIN_WORK_DURATION = Duration.ofHours(8);
    private static final LocalTime CHECK_IN_START_TIME = LocalTime.of(6, 0);
    private static final LocalTime CHECK_IN_END_TIME = LocalTime.of(10, 0);


    public AttendanceManagementServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository, AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    public AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest) {
        Employee employee = employeeRepository.findById(createRequest.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(createRequest.employeeId()));

        if (attendanceRepository.existsByEmployeeIdAndDate(createRequest.employeeId(), createRequest.date())) {
            throw new AttendanceAlreadyExistsException(createRequest.employeeId(), createRequest.date());
        }

        validateCheckInTime(createRequest.checkInTime());

        if (createRequest.checkOutTime() != null) {
            validateCheckOutTime(createRequest.checkInTime(), createRequest.checkOutTime());
        }

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .date(createRequest.date())
                .checkInTime(createRequest.checkInTime())
                .checkOutTime(createRequest.checkOutTime())
                .build();

        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceDetailResponse autoCheckIn(String employeeEmail) {
        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (!isValidCheckInTime(now)) {
            throw new InvalidAttendanceTimeException("Check-in time must be between 06:00 and 10:00");
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
    public AttendanceDetailResponse autoCheckOut(String employeeEmail) {
        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today)
                .orElseThrow(() -> new AttendanceNotFoundException("No attendance found for today " + employee.getId()));

        LocalTime now = LocalTime.now();

        if (attendance.getCheckOutTime() != null) {
            throw new AlreadyCheckedOutException("Already checked out today");
        }

        validateCheckOutTime(attendance.getCheckInTime(), now);

        attendance.setCheckOutTime(now);
        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(attendance));
    }

    @Override
    public AttendanceDetailResponse updateAttendance(UUID id, AttendanceUpdateRequest updateRequest) {
        Attendance toUpdateAttendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance with id: " + id));

        if (updateRequest.checkInTime() != null) {
            validateCheckInTime(updateRequest.checkInTime());
            toUpdateAttendance.setCheckInTime(updateRequest.checkInTime());
        }

        if (updateRequest.checkOutTime() != null) {
            LocalTime effectiveCheckInTime = updateRequest.checkInTime() != null ?
                    updateRequest.checkInTime() : toUpdateAttendance.getCheckInTime();
            validateCheckOutTime(effectiveCheckInTime, updateRequest.checkOutTime());
            toUpdateAttendance.setCheckOutTime(updateRequest.checkOutTime());
        }

        return attendanceMapper.attendanceToAttendanceDetailResponse(attendanceRepository.save(toUpdateAttendance));
    }


    @Override
    public void deleteAttendance(UUID id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceNotFoundException("No attendance found for id " + id));
        attendanceRepository.delete(attendance);
    }

    /**
     * Checks if check-in time is within valid range
     *
     * @param time time to check
     * @return true if valid, false otherwise
     */
    private boolean isValidCheckInTime(LocalTime time) {
        return !time.isBefore(CHECK_IN_START_TIME) && !time.isAfter(CHECK_IN_END_TIME);
    }

    /**
     * Checks if the check-in time is valid (between 06:00-10:00)
     *
     * @param time time to check
     */
    private void validateCheckInTime(LocalTime time) {
        if (!isValidCheckInTime(time)) {
            throw new InvalidAttendanceTimeException("Check-in time must be between 06:00 and 10:00");
        }
    }


    /**
     * Checks if the check-out time is valid
     *
     * @param checkInTime  Check-in time
     * @param checkOutTime Check-out time
     */
    private void validateCheckOutTime(LocalTime checkInTime, LocalTime checkOutTime) {
        if (!checkOutTime.isAfter(checkInTime)) {
            throw new InvalidAttendanceTimeException("Check-out time must be after check-in time");
        }
        if (!isMinimumWorkDurationMet(checkInTime, checkOutTime)) {
            throw new InvalidAttendanceTimeException("Check-out time must be at least 8 hours after check-in time");
        }
    }

    /**
     * Checks: Has at least the minimum working time passed between check-in and check-out times?
     *
     * @param checkInTime  Check-in time
     * @param checkOutTime Check-out time
     * @return True if the minimum working time has passed, false otherwise
     */
    private boolean isMinimumWorkDurationMet(LocalTime checkInTime, LocalTime checkOutTime) {
        Duration duration = Duration.between(checkInTime, checkOutTime);
        return duration.compareTo(MIN_WORK_DURATION) >= 0;
    }
}
