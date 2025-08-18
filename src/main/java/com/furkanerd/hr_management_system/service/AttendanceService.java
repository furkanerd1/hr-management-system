package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;

import java.util.List;

public interface AttendanceService {

    List<ListAttendanceResponse> listAllAttendance();

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(String employeeEmail);

    AttendanceDetailResponse autoCheckOut(String employeeEmail);

    List<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail);
}
