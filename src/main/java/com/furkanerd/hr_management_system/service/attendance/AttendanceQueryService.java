package com.furkanerd.hr_management_system.service.attendance;

import com.furkanerd.hr_management_system.model.dto.request.attendance.AttendanceFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;

import java.util.UUID;

public interface AttendanceQueryService {

    PaginatedResponse<ListAttendanceResponse> listAllAttendance(int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest);

    AttendanceDetailResponse getAttendanceById(UUID id);

    PaginatedResponse<ListAttendanceResponse> getAttendanceByEmployee(String employeeEmail,int page,int size,String sortBy,String sortDirection,AttendanceFilterRequest filterRequest);

    PaginatedResponse<ListAttendanceResponse> getAllAttendanceByEmployee(UUID id, int page, int size, String sortBy, String sortDirection, AttendanceFilterRequest filterRequest);

}
