package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    List<ListAttendanceResponse> listAllAttendance();

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(UUID employeeId);

    AttendanceDetailResponse autoCheckOut(UUID employeeId);
}
