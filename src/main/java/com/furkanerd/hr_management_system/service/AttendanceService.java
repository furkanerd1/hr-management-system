package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    List<ListAttendanceResponse> listAllAttendance();

    AttendanceDetailResponse getAttendanceById(UUID id);

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(String employeeEmail);

    AttendanceDetailResponse autoCheckOut(String employeeEmail);

    AttendanceDetailResponse updateAttendance(UUID id,AttendanceUpdateRequest updateRequest);

    List<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail);

    void deleteAttendance(UUID id);

    List<ListAttendanceResponse> getAttendanceByEmployeeId(UUID id);
}
