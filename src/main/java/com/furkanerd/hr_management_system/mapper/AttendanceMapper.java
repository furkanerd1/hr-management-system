package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.attendance.AttendanceDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.attendance.ListAttendanceResponse;
import com.furkanerd.hr_management_system.model.entity.Attendance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttendanceMapper {

    @Mapping(target = "employeeId",source = "attendance.employee.id")
    @Mapping(target = "employeeFullName",source = "java(getFullName(attendance))")
    ListAttendanceResponse attendancesToListAttendanceResponse(Attendance attendance);

    List<ListAttendanceResponse> attendancesToListAttendanceResponse(List<Attendance> attendances);

    @Mapping(target = "employeeId",source = "attendance.employee.id")
    @Mapping(target = "employeeFullName",source = "java(getFullName(attendance))")
    @Mapping(target = "email",source = "attendance.employee.email")
    @Mapping(target = "departmentName",source = "attendance.employee.department.name")
    @Mapping(target = "positionName",source = "attendance.employee.position.title")
    AttendanceDetailResponse attendanceToAttendanceDetailResponse(Attendance attendance);

    default String getFullName(Attendance attendance) {
        return attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName();
    }

}
