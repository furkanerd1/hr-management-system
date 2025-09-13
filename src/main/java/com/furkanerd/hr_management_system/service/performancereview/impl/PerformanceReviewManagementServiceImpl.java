package com.furkanerd.hr_management_system.service.performancereview.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.mapper.PerformanceReviewMapper;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.PerformanceReviewRepository;
import com.furkanerd.hr_management_system.service.performancereview.PerformanceReviewManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
class PerformanceReviewManagementServiceImpl implements PerformanceReviewManagementService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewMapper performanceReviewMapper;

    PerformanceReviewManagementServiceImpl(PerformanceReviewRepository performanceReviewRepository, EmployeeRepository employeeRepository, PerformanceReviewMapper performanceReviewMapper) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeRepository = employeeRepository;
        this.performanceReviewMapper = performanceReviewMapper;
    }

    @Override
    public PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest createRequest, String email) {
        Employee employee = employeeRepository.findById(createRequest.employeeId()).orElseThrow(() -> new EmployeeNotFoundException(createRequest.employeeId()));

        Employee reviewer = employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException(email));

        if (employee.getId().equals(reviewer.getId())) {
            throw new SelfReviewNotAllowedException("Employee cannot review themselves");
        }

        LocalDate reviewDate = createRequest.reviewDate() != null ? createRequest.reviewDate() : LocalDate.now();
        if (reviewDate.isAfter(LocalDate.now())) {
            throw new InvalidReviewDateException("Review date cannot be in the future");
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

    @Override
    public PerformanceReviewDetailResponse updatePerformanceReview(UUID id, PerformanceReviewUpdateRequest updateRequest, String reviewerEmail) {
        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Performance review not found with id :" + id));

        Employee reviewer = employeeRepository.findByEmail(reviewerEmail).orElseThrow(() -> new EmployeeNotFoundException(reviewerEmail));


        if (!performanceReview.getReviewer().getId().equals(reviewer.getId())) {
            throw new UnauthorizedActionException("You can only update reviews you created");
        }

        performanceReview.setRating(updateRequest.rating());
        performanceReview.setComments(updateRequest.comments());

        LocalDate reviewDate = updateRequest.reviewDate() != null ? updateRequest.reviewDate() : LocalDate.now();
        if (reviewDate.isAfter(LocalDate.now())) {
            throw new InvalidReviewDateException("Review date cannot be in the future");
        }

        performanceReview.setReviewDate(reviewDate);
        return performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReviewRepository.save(performanceReview));
    }

    @Override
    public void deletePerformanceReview(UUID id, String reviewerEmail) {
        Employee reviewer = employeeRepository.findByEmail(reviewerEmail).orElseThrow(() -> new EmployeeNotFoundException(reviewerEmail));
        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Performance review not found with id :" + id));

        if (!performanceReview.getReviewer().getId().equals(reviewer.getId())) {
            throw new UnauthorizedActionException("You can only delete reviews you created");
        }

        performanceReviewRepository.delete(performanceReview);
    }
}
