package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    PaginatedResponse<ListAttendanceResponse> listAllAttendance(int page,int size,String sortBy,String sortDirection);

    AttendanceDetailResponse getAttendanceById(UUID id);

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(String employeeEmail);

    AttendanceDetailResponse autoCheckOut(String employeeEmail);

    AttendanceDetailResponse updateAttendance(UUID id,AttendanceUpdateRequest updateRequest);

    PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail,int page,int size,String sortBy,String sortDirection);

    void deleteAttendance(UUID id);

    PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployeeId(UUID id, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest);
}
