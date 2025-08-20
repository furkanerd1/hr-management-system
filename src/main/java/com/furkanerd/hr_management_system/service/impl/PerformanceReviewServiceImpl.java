package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.InvalidReviewDateException;
import com.furkanerd.hr_management_system.exception.PerformanceReviewNotFoundException;
import com.furkanerd.hr_management_system.exception.SelfReviewNotAllowedException;
import com.furkanerd.hr_management_system.mapper.PerformanceReviewMapper;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.repository.PerformanceReviewRepository;
import com.furkanerd.hr_management_system.service.EmployeeService;
import com.furkanerd.hr_management_system.service.PerformanceReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final PerformanceReviewMapper performanceReviewMapper;
    private final EmployeeService employeeService;

    public PerformanceReviewServiceImpl(PerformanceReviewRepository performanceReviewRepository, PerformanceReviewMapper performanceReviewMapper, EmployeeService employeeService) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.performanceReviewMapper = performanceReviewMapper;
        this.employeeService = employeeService;
    }

    @Override
    public List<ListPerformanceReviewResponse> listAllPerformanceReviews() {
        return performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(performanceReviewRepository.findAll());
    }

    @Override
    public PerformanceReviewDetailResponse getPerformanceReview(UUID id) {

        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("performance review not found with id :"+ id));

        return  performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReview);
    }

    @Override
    public PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest createRequest, String email) {

        Employee employee = employeeService.getEmployeeEntityById(createRequest.employeeId());

        Employee reviewer= employeeService.getEmployeeEntityByEmail(email);

        if (employee.getId().equals(reviewer.getId())) {
            throw new SelfReviewNotAllowedException("Employee cannot review themselves");
        }

        LocalDate reviewDate = LocalDate.now();
        if(createRequest.reviewDate() != null) {
            if(createRequest.reviewDate().isBefore(LocalDate.now())) {
                reviewDate = createRequest.reviewDate();
            }else {
                throw new InvalidReviewDateException("Review date cannot be in the future");
            }
        }

        // create
        PerformanceReview performanceReview = PerformanceReview.builder()
                .employee(employee)
                .reviewer(reviewer)
                .rating(createRequest.rating())
                .comments(createRequest.comments())
                .reviewDate(reviewDate)
                .build();

        return performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReviewRepository.save(performanceReview));
    }
}