package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;



import java.util.UUID;

public interface AttendanceService {

    PaginatedResponse<ListAttendanceResponse> listAllAttendance(int page,int size,String sortBy,String sortDirection,AttendanceFilterRequest filterRequest);

    AttendanceDetailResponse getAttendanceById(UUID id);

    AttendanceDetailResponse createAttendance(AttendanceCreateRequest createRequest);

    AttendanceDetailResponse autoCheckIn(String employeeEmail);

    AttendanceDetailResponse autoCheckOut(String employeeEmail);

    AttendanceDetailResponse updateAttendance(UUID id,AttendanceUpdateRequest updateRequest);

    PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail,int page,int size,String sortBy,String sortDirection,AttendanceFilterRequest filterRequest);

    void deleteAttendance(UUID id);

    PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployeeId(UUID id, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest);
}
