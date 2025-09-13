package com.furkanerd.hr_management_system.service.attendance;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;

import java.util.UUID;

public interface AttendanceManagementService {

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(String employeeEmail);

    AttendanceDetailResponse autoCheckOut(String employeeEmail);

    AttendanceDetailResponse updateAttendance(UUID id, AttendanceUpdateRequest updateRequest);

    void deleteAttendance(UUID id);
}
