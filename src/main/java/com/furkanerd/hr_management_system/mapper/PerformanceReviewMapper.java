package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerformanceReviewMapper {

    @Mapping(target = "employeeId",source = "performanceReview.employee.id")
    @Mapping(target = "employeeFullName",expression = "java(getEmployeeFullName(performanceReview))")
    @Mapping(target = "reviewerId",source = "performanceReview.reviewer.id")
    @Mapping(target = "reviewerFullName",expression = "java(getReviewerFullName(performanceReview))")
    ListPerformanceReviewResponse performanceReviewToListPerformanceReviewResponse(PerformanceReview performanceReview);

    List<ListPerformanceReviewResponse> performanceReviewsToListPerformanceReviewListResponse(List<PerformanceReview> performanceReviews);


    @Mapping(target = "employeeId",source = "performanceReview.employee.id")
    @Mapping(target = "employeeFullName",expression = "java(getEmployeeFullName(performanceReview))")
    @Mapping(target = "email",source = "performanceReview.employee.email")
    @Mapping(target = "departmentName",source = "performanceReview.employee.department.name")
    @Mapping(target = "positionName",source = "performanceReview.employee.position.title")
    @Mapping(target = "managerFullName",expression = "java(getManagerFullName(performanceReview))")
    @Mapping(target = "reviewerFullName",expression = "java(getReviewerFullName(performanceReview))")
    PerformanceReviewDetailResponse  performanceReviewToPerformanceReviewDetailResponse(PerformanceReview performanceReview);

    default String getEmployeeFullName(PerformanceReview performanceReview) {
        return performanceReview.getEmployee().getFirstName() + " " + performanceReview.getEmployee().getLastName();
    }

    default String getReviewerFullName(PerformanceReview performanceReview) {
        return performanceReview.getReviewer().getFirstName() + " " + performanceReview.getReviewer().getLastName();
    }

    default String getManagerFullName(PerformanceReview performanceReview) {
        Employee manager = performanceReview.getEmployee().getManager();
        if (manager == null) return "No Manager";
        return manager.getFirstName() + " " + manager.getLastName();
    }
}