package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.employee.EmployeeLeaveBalanceResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.LeaveRequestDetailResponse;
import com.furkanerd.hr_management_system.model.dto.response.leaverequest.ListLeaveRequestResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaveRequestMapper {

    @Mapping(target = "employeeId",source = "leaveRequest.employee.id")
    @Mapping(target = "employeeFullName",expression = "java(getFullName(leaveRequest))")
    ListLeaveRequestResponse leaveRequestsToListLeaveRequestResponse(LeaveRequest leaveRequest);

    List<ListLeaveRequestResponse> leaveRequestsToListLeaveRequestResponse(List<LeaveRequest> leaveRequests);

    @Mapping(target = "employeeId",source = "leaveRequest.employee.id")
    @Mapping(target = "employeeFullName",expression = "java(getFullName(leaveRequest))")
    @Mapping(target = "email",source = "leaveRequest.employee.email")
    @Mapping(target = "departmentName",source = "leaveRequest.employee.department.name")
    @Mapping(target = "positionName",source = "leaveRequest.employee.position.title")
    @Mapping(target = "approverName", expression = "java(getApproverFullName(leaveRequest))")
    LeaveRequestDetailResponse leaveRequestToLeaveRequestDetailResponse(LeaveRequest leaveRequest);

    @Mapping(target = "employeeId",source = "id")
    EmployeeLeaveBalanceResponse toEmployeeLeaveBalanceResponse(Employee employee);

    default String getFullName(LeaveRequest leaveRequest) {
        return leaveRequest.getEmployee().getFirstName() + " " + leaveRequest.getEmployee().getLastName();
    }

    default  String getApproverFullName(LeaveRequest leaveRequest) {
        if(leaveRequest.getApprover()==null){
            return "Unknown";
        }
        return leaveRequest.getApprover().getFirstName() + " " + leaveRequest.getApprover().getLastName();
    }

}
